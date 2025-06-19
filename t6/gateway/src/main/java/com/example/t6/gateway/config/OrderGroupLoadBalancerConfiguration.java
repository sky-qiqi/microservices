package com.example.t6.gateway.config; // 使用你的实际包名

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

// 这个配置类只为 "order-group" LoadBalancer 提供 Bean
@Configuration
public class OrderGroupLoadBalancerConfiguration {

    @Bean
    public ServiceInstanceListSupplier orderGroupServiceInstanceListSupplier() {
        List<ServiceInstance> instances = Arrays.asList(
                // 3 instances for provider-ord1 (weight 3)
                new StaticServiceInstance("provider-ord1-instance-1", "order-group", "127.0.0.1", 8081, false),
                new StaticServiceInstance("provider-ord1-instance-2", "order-group", "127.0.0.1", 8081, false),
                new StaticServiceInstance("provider-ord1-instance-3", "order-group", "127.0.0.1", 8081, false),
                // 7 instances for provider-ord2 (weight 7)
                new StaticServiceInstance("provider-ord2-instance-1", "order-group", "127.0.0.1", 8083, false),
                new StaticServiceInstance("provider-ord2-instance-2", "order-group", "127.0.0.1", 8083, false),
                new StaticServiceInstance("provider-ord2-instance-3", "order-group", "127.0.0.1", 8083, false),
                new StaticServiceInstance("provider-ord2-instance-4", "order-group", "127.0.0.1", 8083, false),
                new StaticServiceInstance("provider-ord2-instance-5", "order-group", "127.0.0.1", 8083, false),
                new StaticServiceInstance("provider-ord2-instance-6", "order-group", "127.0.0.1", 8083, false),
                new StaticServiceInstance("provider-ord2-instance-7", "order-group", "127.0.0.1", 8083, false)
        );

        return new ServiceInstanceListSupplier() {
            @Override
            public String getServiceId() {
                return "order-group";
            }

            @Override
            public Flux<List<ServiceInstance>> get() {
                return Flux.just(instances);
            }
        };
    }

    // Simple ServiceInstance implementation (可以放在一个单独的文件中共享，或者像这样复制一份)
    private static class StaticServiceInstance implements ServiceInstance {
        private final String instanceId;
        private final String serviceId;
        private final String host;
        private final int port;
        private final boolean secure;

        public StaticServiceInstance(String instanceId, String serviceId, String host, int port, boolean secure) {
            this.instanceId = instanceId;
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
            this.secure = secure;
        }

        @Override public String getInstanceId() { return instanceId; }
        @Override public String getServiceId() { return serviceId; }
        @Override public String getHost() { return host; }
        @Override public int getPort() { return port; }
        @Override public boolean isSecure() { return secure; }
        @Override public java.net.URI getUri() { return java.net.URI.create((this.secure ? "https" : "http") + "://" + this.host + ":" + this.port); }
        @Override public java.util.Map<String, String> getMetadata() { return java.util.Collections.emptyMap(); }
        @Override public String getScheme() { return this.secure ? "https" : "http"; }
    }
}