package com.example.storageservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_storage")
public class Storage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String productId;
    private Integer totalCount;
    private Integer usedCount;
    private Integer remainCount;
}