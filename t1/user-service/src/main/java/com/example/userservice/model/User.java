package com.example.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 */
@Data // 自动生成 getter, setter, toString, equals, hashCode 方法
@AllArgsConstructor // 自动生成全参数构造函数
@NoArgsConstructor // 自动生成无参构造函数
public class User {
    private Long id;
    private String name;
    private String email;
}