package com.example.orderservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String orderId;
    private Integer userId;
    private String productId;
    private Integer amount;
    private String status; // CREATED, PAID, CANCELED
    private LocalDateTime createTime;
}