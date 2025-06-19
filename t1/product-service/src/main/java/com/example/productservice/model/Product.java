package com.example.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品实体类
 */
@Data // 自动生成 getter, setter, toString, equals, hashCode 方法
@AllArgsConstructor // 自动生成全参数构造函数
@NoArgsConstructor // 自动生成无参构造函数
public class Product {
    private Long id;
    private String name;
    private Double price;
}