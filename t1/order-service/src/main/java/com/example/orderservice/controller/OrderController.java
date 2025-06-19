package com.example.orderservice.controller;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.Product;
import com.example.orderservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

/**
 * 订单服务控制器，提供订单创建接口
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private UserServiceClient userServiceClient; // 注入用户服务 Feign 客户端

    @Autowired
    private ProductServiceClient productServiceClient; // 注入商品服务 Feign 客户端

    // 可以选择注入 RestTemplate，如果需要直接使用 RestTemplate 进行调用
    // @Autowired
    // private RestTemplate restTemplate;

    /**
     * 订单生产接口
     * 该接口会请求用户信息查询接口和商品查询接口，综合信息生成订单
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 生成的订单信息
     */
    @PostMapping("/create")
    public Order createOrder(@RequestParam Long userId, @RequestParam Long productId) {
        // 1. 通过 Feign 客户端请求用户信息
        User user = userServiceClient.getUserInfo(userId);
        // 如果使用 RestTemplate 进行调用（备用）：
        // User user = restTemplate.getForObject("http://user-service/users/" + userId, User.class);


        // 2. 通过 Feign 客户端请求商品信息
        Product product = productServiceClient.getProductInfo(productId);
        // 如果使用 RestTemplate 进行调用（备用）：
        // Product product = restTemplate.getForObject("http://product-service/products/" + productId, Product.class);


        // 3. 综合用户信息和商品信息，生成订单
        Order order = new Order();
        order.setOrderId(System.currentTimeMillis()); // 简单生成订单ID（生产环境应使用更健壮的ID生成策略）
        order.setUserId(user.getId());
        order.setUserName(user.getName());
        order.setUserEmail(user.getEmail());
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setProductPrice(product.getPrice());
        order.setOrderTime(LocalDateTime.now()); // 设置订单创建时间
        order.setStatus("CREATED"); // 设置订单状态

        return order;
    }
}