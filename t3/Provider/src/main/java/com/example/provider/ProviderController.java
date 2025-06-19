package com.example.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope  // 支持动态配置刷新
public class ProviderController {
    @Value("${a}")
    private String aValue;

    @GetMapping("/getA")
    public String getA() {
        return aValue;
    }
}