package com.example.t6.gateway.config; // 确保是 config 包

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration // 确保有 @Configuration 注解
public class RateLimiterConfig {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterConfig.class); // 添加日志

    // This bean defines how to determine the key for rate limiting.
    // We are using the remote address (IP) of the client.
    // Make sure this bean name matches the 'key-resolver' arg in application.yml
    @Bean // 确保有 @Bean 注解
    public KeyResolver remoteAddressKeyResolver() { // Bean 名称是 "remoteAddressKeyResolver"
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                // Resolve the client's IP address
                String key = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
                log.debug("Rate Limiter Key Resolver: Resolving key for request from IP: {}", key); // 添加日志
                return Mono.just(key);
            }
        };
    }

    // 其他 Rate Limiter 相关的 Bean 如果有的话也放在这里
    // ...
}