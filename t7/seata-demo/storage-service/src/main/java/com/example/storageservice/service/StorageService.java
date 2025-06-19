package com.example.storageservice.service;

import com.example.storageservice.mapper.StorageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageService {

    @Autowired
    private StorageMapper storageMapper;

    /**
     * 扣减库存操作
     * @param productId 商品ID
     * @param amount 扣减数量
     */
    public String decreaseStorage(String productId, Integer amount) {
        log.info("Storage Service: 准备扣减库存，商品ID: {}, 数量: {}", productId, amount);
        log.info("Storage Service: 当前XID: {}", io.seata.core.context.RootContext.getXID());

        int result = storageMapper.decreaseStorage(productId, amount);
        if (result == 0) {
            log.warn("Storage Service: 库存扣减失败或库存不足，商品ID: {}", productId);
            // 这里不抛出异常，因为要模拟 Order 服务抛异常回滚。
            // 实际生产中这里应该抛出业务异常或返回失败状态，以便上游感知并处理。
            return "Decrease Storage Failed: Insufficient stock or product not found.";
        }
        log.info("Storage Service: 库存扣减成功，商品ID: {}, 扣减数量: {}", productId, amount);
        return "Decrease Storage Success!";
    }
}