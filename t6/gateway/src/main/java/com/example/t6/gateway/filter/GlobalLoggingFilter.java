package com.example.t6.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoggingFilter.class);
    private static final String START_TIME = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());

        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().toString();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        String ip = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "N/A";

        log.info("Global Filter - Request: IP={}, Method={}, URL={}, Headers={}",
                ip, request.getMethod(), url, request.getHeaders());

        // Check if the body has been cached by CachedRequestBodyGatewayFilter
        Object cachedBody = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);

        // To log the body here, the CachedRequestBodyGatewayFilter *must* run before this filter.
        // If it has, the cached body will be in the exchange attributes.
        if (cachedBody instanceof DataBuffer) {
            DataBuffer body = (DataBuffer) cachedBody;
            // Read the body content
            byte[] bytes = new byte[body.readableByteCount()];
            body.read(bytes);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            // IMPORTANT: Put the data buffer back so subsequent filters can read it
            // This releases the original buffer and creates a new one from the bytes read.
            DataBufferUtils.release(body);
            DataBuffer newBody = exchange.getResponse().bufferFactory().wrap(bytes);
            exchange.getAttributes().put(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR, newBody);

            log.info("Global Filter - Cached Request Body: {}", bodyString);

        } else if (cachedBody instanceof Flux) {
            // If cachedBody is a Flux, you'd typically re-cache or consume it here.
            // For simplicity, we assume it's cached as a single DataBuffer by the factory.
            log.debug("Global Filter - Cached body is Flux<DataBuffer>, simple logging skipped.");
        }
        else {
            // If body wasn't cached, or is not a DataBuffer, we can't easily log it here without consuming it
            log.debug("Global Filter - Request body not cached, skipping body logging.");
        }


        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            long duration = (startTime != null) ? (System.currentTimeMillis() - startTime) : -1;
            log.info("Global Filter - Response: Status={}, Duration={}ms, URL={}",
                    exchange.getResponse().getStatusCode(), duration, url);
        }));
    }

    @Override
    public int getOrder() {
        // Set an order to run early. Run slightly after CachedRequestBodyFilter (default order 0)
        // if you need to log the body using the cached attribute.
        // If body logging is not essential at this exact point, run at HIGHEST_PRECEDENCE.
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}