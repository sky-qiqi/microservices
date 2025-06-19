// 在 commonfeign 模块的 com.example.commonfeign.client 包下创建

package com.example.commonfeign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

// 这里的 name 属性要与 Provider 注册到 Eureka 的 serviceId 一致
// fallbackFactory 指定当 Feign 调用失败时，由哪个工厂类创建 fallback 实例
@FeignClient(name = "provider-service", fallbackFactory = ProviderClientFallbackFactory.class)
public interface ProviderClient {

    // 这个方法对应 Provider 的接口，参数和返回值类型需要匹配
    @GetMapping("/provider/data/{id}") // 假设 Provider 的接口路径是 /provider/data/{id}
    Map<String, Object> getData(@PathVariable("id") int id);
}