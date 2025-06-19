// 在 com.example.consumer.config 包下创建 TimeBasedInstanceLoadBalancer.java

package com.example.consumer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

// 这个类只负责实现负载均衡的逻辑
public class TimeBasedInstanceLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Logger log = LoggerFactory.getLogger(TimeBasedInstanceLoadBalancer.class);

    private final String serviceId;
    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    private final Random random = new Random();

    // 构造函数用于接收 serviceId 和 实例列表供应商
    public TimeBasedInstanceLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
                                         String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
        // 使用 flatMap 操作 ServiceInstanceListSupplier 的 Mono<List<ServiceInstance>>
        return supplier.get(request).next().map(this::getInstanceResponse);
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            log.warn("No instances available for service: {}", serviceId);
            return new EmptyResponse();
        }

        if (instances.size() == 1) {
            log.debug("Only one instance available for {}, returning it.", serviceId);
            return new DefaultResponse(instances.get(0));
        }

        // 你的自定义负载均衡逻辑：根据当前时间的秒数 % 3 + 1 选择实例
        int seconds = LocalTime.now().getSecond();
        // 目标实例编号 1, 2, 或 3
        int targetInstanceNum = (seconds % 3) + 1;
        // 假设端口尾数就是实例编号 (例如 8081 -> 1, 8082 -> 2)
        int targetPortSuffix = targetInstanceNum;

        ServiceInstance selectedInstance = null;
        for (ServiceInstance instance : instances) {
            int port = instance.getPort();
            if ((port % 10) == targetPortSuffix) {
                selectedInstance = instance;
                log.info("Custom LB: Selected instance based on time ({}s -> target {} -> port {}): {}",
                        seconds, targetInstanceNum, port, instance.getInstanceId());
                break;
            }
        }

        if (selectedInstance == null) {
            // 如果根据端口尾数没有找到匹配的实例，回退到使用基于秒数的索引选择
            int indexToSelect = seconds % instances.size(); // 使用可用实例数量取模更健壮
            selectedInstance = instances.get(indexToSelect);
            log.warn("Custom LB: Could not find instance matching port suffix {}. Falling back to index {}: {}",
                    targetPortSuffix, indexToSelect, selectedInstance.getInstanceId());
        }

        return new DefaultResponse(selectedInstance);
    }
}