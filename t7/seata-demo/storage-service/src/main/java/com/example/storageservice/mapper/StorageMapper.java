package com.example.storageservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.storageservice.entity.Storage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // <-- 导入 @Param 注解
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StorageMapper extends BaseMapper<Storage> {

    /**
     * 扣减库存
     * @param productId 商品ID
     * @param amount 扣减数量
     * @return 影响的行数
     */
    @Update("UPDATE t_storage SET used_count = used_count + #{amount}, remain_count = remain_count - #{amount} WHERE product_id = #{productId} AND remain_count >= #{amount}")
    // **核心修改：为参数添加 @Param 注解**
    int decreaseStorage(@Param("productId") String productId, @Param("amount") Integer amount);
}