package com.example.orderservice.client;

import com.example.orderservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign 客户端接口，用于调用商品服务
 * 通过 @FeignClient 注解指定调用的服务名称（在 Eureka 中注册的名称）
 */
@FeignClient(name = "product-service")
public interface ProductServiceClient {

    /**
     * 调用商品服务的商品信息查询接口
     * @param productId 商品ID
     * @return 商品信息
     */
    @GetMapping("/products/{productId}")
    Product getProductInfo(@PathVariable("productId") Long productId);
}