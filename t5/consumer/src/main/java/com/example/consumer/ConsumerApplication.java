package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan; // 导入 ComponentScan

@SpringBootApplication
@EnableDiscoveryClient
// 仍然需要 EnableFeignClients 来扫描 Feign 接口本身
@EnableFeignClients(basePackages = "com.example.commonfeign.client")
// 添加 ComponentScan，包含 Consumer 的基础包和 commonfeign 的包
@ComponentScan(basePackages = {"com.example.consumer", "com.example.commonfeign.client"})
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}