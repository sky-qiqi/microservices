package com.example.t6.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// Abstract base class for filters that need to modify the request body (specifically JSON)
// Requires CachedRequestBodyGatewayFilter to be applied before it.
public abstract class ModifyRequestBodyGatewayFilterFactory<C> extends AbstractGatewayFilterFactory<C> {

    private static final Logger log = LoggerFactory.getLogger(ModifyRequestBodyGatewayFilterFactory.class);
    private final List<org.springframework.http.codec.HttpMessageReader<?>> httpMessageReaders;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor requiring the config class type from subclasses
    // This constructor is typically used when ServerCodecConfigurer is injected by Spring
    protected ModifyRequestBodyGatewayFilterFactory(Class<C> configClass, ServerCodecConfigurer codecConfigurer) {
        super(configClass);
        this.httpMessageReaders = codecConfigurer.getReaders();
    }

    // Provide a constructor for backward compatibility or manual setup, though injection is preferred
    protected ModifyRequestBodyGatewayFilterFactory(Class<C> configClass) {
        super(configClass);
        // Default codec configurer - might not be ideal in all contexts, injection is preferred
        ServerCodecConfigurer codecConfigurer = ServerCodecConfigurer.create();
        this.httpMessageReaders = codecConfigurer.getReaders();
    }


    @Override
    public GatewayFilter apply(C config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpMethod method = request.getMethod();

            // Only attempt to modify body for methods that typically have one
            if (method != HttpMethod.POST && method != HttpMethod.PUT && method != HttpMethod.PATCH) {
                log.debug("Skipping body modification for method: {}", method);
                return chain.filter(exchange);
            }

            // Check if the body content type is applicable (e.g., JSON)
            MediaType contentType = request.getHeaders().getContentType();
            // Also check for specific JSON subtypes like application/vnd.api+json if needed
            if (contentType == null || !MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                log.debug("Skipping body modification for non-JSON compatible content type: {}", contentType);
                return chain.filter(exchange);
            }


            // Retrieve the cached body from exchange attributes
            // CachedRequestBodyGatewayFilter puts a DataBuffer or Flux<DataBuffer> here.
            Object cachedBody = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);

            if (cachedBody == null) {
                // Body is not cached, cannot read/modify.
                log.warn("Cached request body not found. Ensure CachedRequestBodyGatewayFilter runs before this filter.");
                return chain.filter(exchange);
            }

            Flux<DataBuffer> bodyFlux;
            if (cachedBody instanceof DataBuffer) {
                // If it's a single DataBuffer, wrap it in a Flux
                bodyFlux = Flux.just((DataBuffer) cachedBody);
            } else if (cachedBody instanceof Flux) {
                // If it's already a Flux, use it directly
                bodyFlux = (Flux<DataBuffer>) cachedBody;
            } else {
                log.error("Unsupported cached body type: {}", cachedBody.getClass());
                return chain.filter(exchange);
            }

            // Use the DataBufferFactory from the exchange's response
            DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();


            // Read the body into a byte array, then parse JSON
            // Use DataBufferUtils.join to collect all buffers
            Mono<byte[]> cachedBodyBytesMono = DataBufferUtils.join(bodyFlux)
                    .map(dataBuffer -> {
                        // Read bytes from the joined buffer
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer); // Release the buffer after reading
                        return bytes;
                    })
                    // Handle the case of an empty body
                    .defaultIfEmpty(new byte[0])
                    // Ensure the original buffer is released even if there's an error
                    .doOnError(e -> DataBufferUtils.release(bodyFlux.cache().blockFirst())) // Attempt to release if join fails
                    .onErrorResume(e -> {
                        log.error("Error joining data buffers", e);
                        // If joining fails, return an empty byte array or handle as needed
                        return Mono.just(new byte[0]);
                    });


            return cachedBodyBytesMono.flatMap(bytes -> {
                JsonNode originalJsonNode;
                try {
                    // Attempt to parse as JSON from bytes
                    originalJsonNode = objectMapper.readTree(bytes);
                } catch (Exception e) {
                    log.error("Error parsing request body as JSON", e);
                    // If parsing fails, continue with the original request/body bytes
                    Flux<DataBuffer> originalBytesFlux = Flux.just(bufferFactory.wrap(bytes));
                    ServerHttpRequest originalBodyRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return originalBytesFlux;
                        }
                        @Override
                        public HttpHeaders getHeaders() {
                            HttpHeaders decoratedHeaders = new HttpHeaders();
                            decoratedHeaders.putAll(super.getHeaders());
                            // Update Content-Length if possible, though it might be tricky if original wasn't accurate
                            decoratedHeaders.setContentLength(bytes.length);
                            return decoratedHeaders;
                        }
                    };
                    // Put original bytes back in case other filters need it
                    exchange.getAttributes().put(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR, originalBytesFlux);

                    return chain.filter(exchange.mutate().request(originalBodyRequest).build());
                }


                // Modify the JSON node (implemented by subclasses)
                JsonNode modifiedJsonNode = modifyBody(originalJsonNode, config);

                // If modifyBody returns null, it indicates no modification or an error handled by subclass
                if (modifiedJsonNode == null) {
                    log.debug("modifyBody returned null. Proceeding with original body.");
                    // Put original bytes back in case other filters need it
                    Flux<DataBuffer> originalBytesFlux = Flux.just(bufferFactory.wrap(bytes));
                    ServerHttpRequest originalBodyRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return originalBytesFlux;
                        }
                        @Override
                        public HttpHeaders getHeaders() {
                            HttpHeaders decoratedHeaders = new HttpHeaders();
                            decoratedHeaders.putAll(super.getHeaders());
                            decoratedHeaders.setContentLength(bytes.length);
                            return decoratedHeaders;
                        }
                    };
                    exchange.getAttributes().put(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR, originalBytesFlux);
                    return chain.filter(exchange.mutate().request(originalBodyRequest).build());
                }


                // Serialize the modified JSON back to bytes
                byte[] modifiedBytes;
                try {
                    modifiedBytes = objectMapper.writeValueAsBytes(modifiedJsonNode);
                } catch (Exception e) {
                    log.error("Error serializing modified JSON body", e);
                    // Handle serialization failure - perhaps proceed with original or return error
                    // Put original bytes back in case other filters need it
                    Flux<DataBuffer> originalBytesFlux = Flux.just(bufferFactory.wrap(bytes));
                    ServerHttpRequest originalBodyRequest = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return originalBytesFlux;
                        }
                        @Override
                        public HttpHeaders getHeaders() {
                            HttpHeaders decoratedHeaders = new HttpHeaders();
                            decoratedHeaders.putAll(super.getHeaders());
                            decoratedHeaders.setContentLength(bytes.length);
                            return decoratedHeaders;
                        }
                    };
                    exchange.getAttributes().put(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR, originalBytesFlux);
                    return chain.filter(exchange.mutate().request(originalBodyRequest).build());
                }

                // Create a new DataBuffer from the modified bytes
                Flux<DataBuffer> modifiedBodyFlux = Flux.just(bufferFactory.wrap(modifiedBytes));

                // Create a new request decorator with the modified body
                ServerHttpRequest modifiedBodyRequest = new ServerHttpRequestDecorator(request) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return modifiedBodyFlux; // Use the modified body flux
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders decoratedHeaders = new HttpHeaders();
                        decoratedHeaders.putAll(super.getHeaders());
                        // Update Content-Length header
                        decoratedHeaders.setContentLength(modifiedBytes.length);
                        return decoratedHeaders;
                    }
                };

                // Replace the cached body attribute with the modified body flux
                // This ensures subsequent filters/handlers read the modified body.
                // It's important to release the original cached buffer(s) here if they are not used further.
                // Since we read the original body into bytes, we should have released it.
                // The modifiedBodyFlux will now be cached for downstream filters.
                exchange.getAttributes().put(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR, modifiedBodyFlux);


                // Continue the filter chain with the decorated request containing the modified body
                return chain.filter(exchange.mutate().request(modifiedBodyRequest).build());

            });
        };
    }

    // Abstract method to be implemented by subclasses for body modification
    // Should return the modified JsonNode. Return the original node or handle
    // error if modification fails within the subclass logic.
    protected abstract JsonNode modifyBody(JsonNode originalBody, C config);
}