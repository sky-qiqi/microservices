package com.example.orderservice.client;

import com.example.orderservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign 客户端接口，用于调用用户服务
 * 通过 @FeignClient 注解指定调用的服务名称（在 Eureka 中注册的名称）
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {

    /**
     * 调用用户服务的用户信息查询接口
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/users/{userId}")
    User getUserInfo(@PathVariable("userId") Long userId);
}