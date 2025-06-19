package com.example.webfront.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单实体类（此文件在 web-front 中，用于接收 order-service 返回的数据）
 * 实际项目中可以考虑将通用 Model 放入一个独立的 common 模块
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long orderId;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long productId;
    private String productName;
    private Double productPrice;
    private LocalDateTime orderTime;
    private String status;
}