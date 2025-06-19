package com.example.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data // 自动生成 getter, setter, toString, equals, hashCode 方法
@AllArgsConstructor // 自动生成全参数构造函数
@NoArgsConstructor // 自动生成无参构造函数
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