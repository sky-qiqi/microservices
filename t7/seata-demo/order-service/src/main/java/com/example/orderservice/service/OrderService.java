package com.example.orderservice.service;

import com.example.orderservice.entity.Order;
import com.example.orderservice.feign.StorageFeignClient;
import com.example.orderservice.mapper.OrderMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StorageFeignClient storageFeignClient;

    /**
     * 下单并扣减库存
     * @param userId 用户ID
     * @param productId 商品ID
     * @param amount 数量
     * @param flag 是否抛出异常 (1: 抛出异常, 其他: 不抛出)
     * @return
     */
    @GlobalTransactional(name = "place-order-and-decrease-storage", rollbackFor = Exception.class)
    public String placeOrder(Integer userId, String productId, Integer amount, Integer flag) {
        // 1. 创建订单
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setAmount(amount);
        order.setStatus("CREATED");
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);
        log.info("Order Service: 订单创建成功，订单ID: {}", order.getOrderId());
        log.info("Order Service: 当前XID: {}", io.seata.core.context.RootContext.getXID());

        // 2. 调用 Storage 服务扣减库存
        log.info("Order Service: 调用 Storage 服务扣减库存, 商品ID: {}, 数量: {}", productId, amount);
        StorageFeignClient.DeductRequest request = new StorageFeignClient.DeductRequest();
        request.setProductId(productId);
        request.setAmount(amount);
        String storageResult = storageFeignClient.deduct(request);
        log.info("Order Service: Storage 服务返回结果: {}", storageResult);

        // 3. 检查库存扣减结果
        if (!"Decrease Storage Success!".equals(storageResult)) {
            log.error("Order Service: 库存扣减失败，商品ID: {}, 触发全局事务回滚", productId);
            throw new RuntimeException("Failed to deduct storage: " + storageResult);
        }

        // 4. 根据 flag 决定是否抛出异常进行回滚
        if (flag != null && flag == 1) {
            log.error("Order Service: 根据 flag=1，模拟抛出异常，触发全局事务回滚");
            throw new RuntimeException("模拟异常，触发分布式事务回滚");
        }

        log.info("Order Service: 订单和库存操作完成，没有触发回滚");
        return "Order Placed Successfully!";
    }
}