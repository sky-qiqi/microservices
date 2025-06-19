package com.example.webfront;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 网页前端启动类
 */
@SpringBootApplication // 这是一个 Spring Boot 应用
@EnableDiscoveryClient // 启用服务发现客户端，将服务注册到 Eureka
@EnableFeignClients(basePackages = "com.example.webfront.client") // 启用 Feign 客户端，并指定扫描路径
public class WebFrontApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebFrontApplication.class, args);
    }

    /**
     * 定义 RestTemplate Bean，用于非 Feign 的 REST 调用
     * （在本例中，主要使用 Feign，但保留此 Bean 以作备用或示例）
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}