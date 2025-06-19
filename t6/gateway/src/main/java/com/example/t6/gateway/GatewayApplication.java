package com.example.t6.gateway; // 使用你的实际应用包名

import com.example.t6.gateway.config.OrderGroupLoadBalancerConfiguration; // 导入现有配置类
import com.example.t6.gateway.config.UserGroupLoadBalancerConfiguration; // 导入现有配置类
import com.example.t6.gateway.config.ProviderTest1LoadBalancerConfiguration; // 导入现有配置类
import com.example.t6.gateway.config.ProviderTest2LoadBalancerConfiguration; // 导入 **新的** 配置类

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// 移除了不再需要的导入（如 KeyResolver, Bean, Primary 等），因为它们应该在 RateLimiterConfig 或其他特定配置类中定义

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient; // 导入注解
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients; // 导入注解


// 将 @LoadBalancerClients 注解添加到主应用类上
// 现在应该包含 order-group, user-group, provider-test1, 和 provider-test2
@LoadBalancerClients({
        @LoadBalancerClient(name = "order-group", configuration = OrderGroupLoadBalancerConfiguration.class), // 引用 OrderGroup 的配置
        @LoadBalancerClient(name = "user-group", configuration = UserGroupLoadBalancerConfiguration.class),   // 引用 UserGroup 的配置
        @LoadBalancerClient(name = "provider-test1", configuration = ProviderTest1LoadBalancerConfiguration.class), // 引用 ProviderTest1 的配置
        @LoadBalancerClient(name = "provider-test2", configuration = ProviderTest2LoadBalancerConfiguration.class) // 引用 **新的** ProviderTest2 配置
})
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // The remoteAddressKeyResolver bean definition should be in RateLimiterConfig.java
    // Other bean definitions in this class (if any) remain here.
    // 请确保这个类中没有重复定义 remoteAddressKeyResolver Bean
    // ... 其他可能存在的 Bean 定义 ...
}