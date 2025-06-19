package com.example.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类（此文件在 order-service 中，用于接收 user-service 返回的数据）
 * 实际项目中可以考虑将通用 Model 放入一个独立的 common 模块
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}