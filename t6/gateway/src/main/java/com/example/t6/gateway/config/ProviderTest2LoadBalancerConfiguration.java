package com.example.t6.gateway.config; // 使用你的实际包名

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

// 这个配置类只为 "provider-test2" LoadBalancer 提供 Bean
@Configuration
public class ProviderTest2LoadBalancerConfiguration {

    @Bean
    public ServiceInstanceListSupplier providerTest2ServiceInstanceListSupplier() {
        List<ServiceInstance> instances = Arrays.asList(
                // ServiceInstance 的参数: instanceId, serviceId, host, port, isSecure
                new StaticServiceInstance("provider-test2-instance-1", "provider-test2", "127.0.0.1", 8087, false)
        );

        return new ServiceInstanceListSupplier() {
            @Override
            public String getServiceId() {
                return "provider-test2"; // 必须匹配 @LoadBalancerClient 中的 name
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