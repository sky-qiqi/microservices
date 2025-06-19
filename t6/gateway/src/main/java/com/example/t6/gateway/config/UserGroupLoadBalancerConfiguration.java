package com.example.t6.gateway.config; // 使用你的实际包名

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

// 这个配置类只为 "user-group" LoadBalancer 提供 Bean
@Configuration
public class UserGroupLoadBalancerConfiguration {

    @Bean
    public ServiceInstanceListSupplier userGroupServiceInstanceListSupplier() {
        List<ServiceInstance> instances = Arrays.asList(
                // 3 instances for provider-user1 (weight 3)
                new StaticServiceInstance("provider-user1-instance-1", "user-group", "127.0.0.1", 8084, false),
                new StaticServiceInstance("provider-user1-instance-2", "user-group", "127.0.0.1", 8084, false),
                new StaticServiceInstance("provider-user1-instance-3", "user-group", "127.0.0.1", 8084, false),
                // 7 instances for provider-user2 (weight 7)
                new StaticServiceInstance("provider-user2-instance-1", "user-group", "127.0.0.1", 8085, false),
                new StaticServiceInstance("provider-user2-instance-2", "user-group", "127.0.0.1", 8085, false),
                new StaticServiceInstance("provider-user2-instance-3", "user-group", "127.0.0.1", 8085, false),
                new StaticServiceInstance("provider-user2-instance-4", "user-group", "127.0.0.1", 8085, false),
                new StaticServiceInstance("provider-user2-instance-5", "user-group", "127.0.0.1", 8085, false),
                new StaticServiceInstance("provider-user2-instance-6", "user-group", "127.0.0.1", 8085, false),
                new StaticServiceInstance("provider-user2-instance-7", "user-group", "127.0.0.1", 8085, false)
        );

        return new ServiceInstanceListSupplier() {
            @Override
            public String getServiceId() {
                return "user-group";
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