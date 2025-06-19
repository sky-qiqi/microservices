package com.example.config_client_dev;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    // 使用 @Value 注解从远程 Config Server 获取 "name" 配置项的值
    @Value("${name}")
    private String name;

    // 创建一个 GET 请求接口来返回从 Config Server 获取的 "name" 配置项的值
    @GetMapping("/namedev")
    public String getNameFromConfigServer() {
        return "配置文件的name是: " + name;
    }
}