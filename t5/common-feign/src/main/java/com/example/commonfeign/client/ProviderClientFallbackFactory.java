package com.example.commonfeign.client;

import org.springframework.cloud.openfeign.FallbackFactory; // 使用 Spring Cloud OpenFeign 的 FallbackFactory
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component // 将其注册为 Spring Bean
public class ProviderClientFallbackFactory implements FallbackFactory<ProviderClient> {

    private static final Logger log = LoggerFactory.getLogger(ProviderClientFallbackFactory.class);
    private final Random random = new Random();

    @Override
    public ProviderClient create(Throwable cause) {
        log.error("ProviderClient fallback; reason was: {}", cause.getMessage(), cause);

        // 返回一个 ProviderClient 的匿名实现类，定义降级逻辑
        return new ProviderClient() {
            @Override
            public Map<String, Object> getData(int id) {
                log.warn("Executing fallback for getData with id: {}", id);
                Map<String, Object> fallbackResponse = new HashMap<>();
                fallbackResponse.put("error", "Service call failed for id: " + id);
                fallbackResponse.put("message", "Fallback response triggered due to: " + cause.getMessage());
                // 根据需求，当 id=3 时，随机返回 1, 2, 或 3
                if (id == 3) {
                    int fallbackInstanceId = random.nextInt(3) + 1;
                    fallbackResponse.put("fallbackInstanceId", fallbackInstanceId);
                    log.info("Fallback for id=3, returning mock instanceId: {}", fallbackInstanceId);
                }
                return fallbackResponse;
            }
        };
    }
}