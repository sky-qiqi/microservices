// 在 com.example.consumer.config 包下修改 CustomLoadBalancerConfiguration.java

package com.example.consumer.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Environment; // 确保导入 Environment

// 这个类是用于配置自定义 Load Balancer 的 Configuration 类
@Configuration
public class CustomLoadBalancerConfiguration {

    // 定义一个 Bean，类型是 ReactorServiceInstanceLoadBalancer
    @Bean
    public ReactorServiceInstanceLoadBalancer customLoadBalancer(Environment environment, // Spring 会注入 Environment
                                                                 LoadBalancerClientFactory loadBalancerClientFactory // Spring 会注入 LoadBalancerClientFactory
    ) {
        // 从 Environment 获取服务名称
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);

        // 从 LoadBalancerClientFactory 获取 ServiceInstanceListSupplierProvider
        ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider =
                loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class);

        // 创建 TimeBasedInstanceLoadBalancer 的实例，并传入 serviceId 和 supplierProvider
        return new TimeBasedInstanceLoadBalancer(serviceInstanceListSupplierProvider, name);
    }

    // 你的 ConsumerFeignConfig 应该保持不变，因为它已经通过 @LoadBalancerClient 指向了这个 Configuration 类。
}