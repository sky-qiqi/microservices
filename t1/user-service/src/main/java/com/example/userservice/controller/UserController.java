package com.example.userservice.controller;

import com.example.userservice.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务控制器，提供用户信息查询接口
 */
@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 对应的用户信息
     */
    @GetMapping("/{userId}")
    public User getUserInfo(@PathVariable Long userId) {
        // 返回固定用户信息作为模拟数据
        // 实际场景中会从数据库中查询用户信息
        if (userId == 1L) {
            return new User(1L, "张三", "zhangsan@example.com");
        } else if (userId == 2L) {
            return new User(2L, "李四", "lisi@example.com");
        }
        // 如果找不到对应用户，返回一个默认的未知用户
        return new User(userId, "未知用户", "unknown@example.com");
    }
}