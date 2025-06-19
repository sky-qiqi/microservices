package com.example.consumer.config;

import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.env.Environment;

// 将自定义负载均衡配置应用到名为 "provider-service" 的服务
@Configuration
@LoadBalancerClient(name = "provider-service", configuration = CustomLoadBalancerConfiguration.class)
public class ConsumerFeignConfig {

    // Spring Cloud LoadBalancer 会自动根据 @LoadBalancerClient 注解创建
    // CustomLoadBalancerConfiguration 实例，无需显式定义 Bean。
    /*
    @Bean
    ReactorServiceInstanceLoadBalancer customLoadBalancer(Environment environment, LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new CustomLoadBalancerConfiguration(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }
    */

    // 如果你使用 RestTemplate 并希望它也使用这个负载均衡器，可以取消注释下面的 Bean
    // @Bean
    // @LoadBalanced
    // public RestTemplate restTemplate() {
    //     return new RestTemplate();
    // }
}