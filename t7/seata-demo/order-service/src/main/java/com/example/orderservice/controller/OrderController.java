package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Data
    public static class OrderResponse {
        private boolean success;
        private String message;

        public OrderResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    /**
     * 测试下单接口
     * http://localhost:8081/order/placeOrder/test?flag=0  (正常提交)
     * http://localhost:8081/order/placeOrder/test?flag=1  (触发回滚)
     * http://localhost:8081/order/placeOrder/rollback1?flag=1 (触发回滚)
     */
    @GetMapping("/placeOrder/{action}")
    public OrderResponse placeOrder(@PathVariable String action, @RequestParam(value = "flag", required = false) Integer flag) {
        Integer userId = 1;
        String productId = "product-1";
        Integer amount = 1;

        log.info("OrderController: Received action: {}, flag: {}", action, flag);

        // Default flag to 0 if not provided
        if (flag == null) {
            if ("rollback1".equalsIgnoreCase(action)) {
                flag = 1; // Default to rollback for rollback1 action
            } else {
                flag = 0; // Default to commit for other actions
            }
        }

        log.info("OrderController: Final flag value: {}", flag);
        try {
            String result = orderService.placeOrder(userId, productId, amount, flag);
            log.info("OrderController: OrderService result: {}", result);
            return new OrderResponse(true, result);
        } catch (RuntimeException e) {
            String errorMessage = e.getCause() != null && e.getCause().getMessage() != null
                    ? e.getCause().getMessage() : e.getMessage();
            log.error("OrderController: Transaction rolled back with error: {}", errorMessage, e);
            return new OrderResponse(false, "Order placement failed: " + errorMessage);
        }
    }
}