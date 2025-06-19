package com.example.webfront.controller;

import com.example.webfront.client.OrderServiceClient;
import com.example.webfront.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 网页控制器，处理前端页面请求
 */
@Controller
public class WebController {

    @Autowired
    private OrderServiceClient orderServiceClient; // 注入订单服务 Feign 客户端

    /**
     * 显示下单页面
     * @param model Thymeleaf 模型
     * @return 页面名称
     */
    @GetMapping("/")
    public String index(Model model) {
        // 示例数据，实际应用中可以从数据库获取或动态生成
        model.addAttribute("users", "用户1 (ID: 1), 用户2 (ID: 2)");
        model.addAttribute("products", "笔记本电脑 (ID: 101), 机械键盘 (ID: 102)");
        return "index"; // 返回 index.html 模板
    }

    /**
     * 处理下单请求
     * @param userId 用户ID
     * @param productId 商品ID
     * @param model Thymeleaf 模型
     * @return 订单结果页面名称
     */
    @PostMapping("/placeOrder")
    public String placeOrder(@RequestParam Long userId, @RequestParam Long productId, Model model) {
        try {
            // 调用订单服务创建订单
            Order order = orderServiceClient.createOrder(userId, productId);
            model.addAttribute("order", order);
            model.addAttribute("message", "订单创建成功！");
        } catch (Exception e) {
            // 处理异常，例如服务不可用
            model.addAttribute("error", "订单创建失败: " + e.getMessage());
        }
        return "orderResult"; // 返回 orderResult.html 模板
    }
}