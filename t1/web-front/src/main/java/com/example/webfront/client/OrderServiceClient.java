package com.example.webfront.client;

import com.example.webfront.model.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign 客户端接口，用于调用订单服务
 * 通过 @FeignClient 注解指定调用的服务名称（在 Eureka 中注册的名称）
 */
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    /**
     * 调用订单服务创建订单接口
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 创建成功的订单信息
     */
    @PostMapping("/orders/create")
    Order createOrder(@RequestParam("userId") Long userId, @RequestParam("productId") Long productId);
}