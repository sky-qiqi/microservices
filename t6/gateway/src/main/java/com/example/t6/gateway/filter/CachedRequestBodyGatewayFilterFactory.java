package com.example.t6.gateway.filter; // Make sure this package matches your project structure

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// This class is a GatewayFilterFactory that caches the request body.
// It's needed for filters that read the request body (like ModifyRequestBodyGatewayFilterFactory).
@Component // This makes Spring detect this as a bean
public class CachedRequestBodyGatewayFilterFactory extends AbstractGatewayFilterFactory<CachedRequestBodyGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(CachedRequestBodyGatewayFilterFactory.class);

    public CachedRequestBodyGatewayFilterFactory() {
        super(Config.class);
    }

    // Configuration class for this filter factory.
    // Since no specific configuration is needed for simple caching, it can be empty.
    public static class Config {
        // No config properties needed for this filter
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Check if the body has already been cached by a previous filter instance
            Object cachedBody = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
            if (cachedBody != null) {
                // Body is already cached, just continue the filter chain
                log.debug("Request body already cached for path: {}", request.getPath());
                return chain.filter(exchange);
            }

            log.debug("Caching request body for path: {}", request.getPath());

            // Use Spring Cloud Gateway's utility to cache the request body.
            // This reads the body and stores it in an exchange attribute.
            // The method returns Mono<Void> when the caching is complete.
            return ServerWebExchangeUtils.cacheRequestBodyAndRequest(exchange,
                            // The inner lambda receives the decorated request (with cached body).
                            // We return Mono.just(cachedRequest) to satisfy the Function signature,
                            // but the return value of this inner lambda is not directly used by
                            // cacheRequestBodyAndRequest's overall Mono<Void> result.
                            (cachedRequest) -> Mono.just(cachedRequest) // You can return Mono.empty() here as well
                    )
                    // After the caching is complete (the Mono<Void> finishes),
                    // proceed to the next filter in the chain using the *original* exchange object,
                    // which now contains the cached body attribute.
                    .then(chain.filter(exchange))
                    // Handle potential errors during caching
                    .onErrorResume(throwable -> {
                        log.error("Error caching request body for path: {}", request.getPath(), throwable);
                        // If caching fails, still continue the chain with the original exchange.
                        // Downstream filters that require the cached body might fail if caching failed here.
                        return chain.filter(exchange);
                    });
        };
    }
}