package com.example.config_client_pro;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ProClientController {

    @Value("${name}")
    private String name;

    @GetMapping("/namepro")
    public String getName() {
        return "配置文件的name是: " + name;
    }
}