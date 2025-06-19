package com.example.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Server 启动类
 */
@SpringBootApplication // 这是一个 Spring Boot 应用
@EnableEurekaServer    // 启用 Eureka Server 功能
public class EurekaServerApplication {
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}