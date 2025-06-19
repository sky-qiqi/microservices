package com.example.productservice.controller;

import com.example.productservice.model.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品服务控制器，提供商品信息查询接口
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * 根据商品ID查询商品信息
     * @param productId 商品ID
     * @return 对应的商品信息
     */
    @GetMapping("/{productId}")
    public Product getProductInfo(@PathVariable Long productId) {
        // 返回固定商品信息作为模拟数据
        // 实际场景中会从数据库中查询商品信息
        if (productId == 101L) {
            return new Product(101L, "笔记本电脑", 8999.00);
        } else if (productId == 102L) {
            return new Product(102L, "机械键盘", 599.00);
        }
        // 如果找不到对应商品，返回一个默认的未知商品
        return new Product(productId, "未知商品", 0.00);
    }
}