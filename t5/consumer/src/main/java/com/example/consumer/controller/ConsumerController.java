package com.example.consumer.controller;

import com.example.commonfeign.client.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
// 不再需要 Collectors
// import java.util.stream.Collectors;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final ProviderClient providerClient;

    @Autowired
    public ConsumerController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @GetMapping("/data/{id}")
    // 方法返回类型仍然是 String
    public String getProviderData(@PathVariable("id") int id) {
        System.out.println("Consumer received request for id: " + id);

        // 调用 Feign 客户端。返回正常响应的 Map 或 Fallback 响应的 Map。
        Map<String, Object> result = providerClient.getData(id);

        // 仍然将原始的 Map 打印到 Consumer 服务的控制台，方便调试
        System.out.println("Consumer received response Map (or Fallback Map): " + result);

        StringBuilder simpleOutput = new StringBuilder();

        if (result != null && !result.isEmpty()) {
            // 检查返回的 Map 是否包含 "fallbackInstanceId" 键，这是判断是否触发了 fallback 的标记
            if (result.containsKey("fallbackInstanceId")) {
                // === Fallback 响应 ===
                // 提取 fallbackInstanceId
                // Map 存储的是 Object，所以需要类型转换。假设 fallbackInstanceId 存储为 Integer。
                Integer fallbackInstanceId = (Integer) result.get("fallbackInstanceId");

                // 构建输出字符串: "异常 + 对应的1或者2或者3"
                simpleOutput.append("发生异常，数字为").append(fallbackInstanceId).append(" 的降级数据。");

            } else {
                // === 正常 Provider 响应 ===
                // 提取 instanceId 和 currentTime
                // 假设 instanceId 存储为 Integer，currentTime 存储为 String。
                Integer instanceIdFromProvider = (Integer) result.get("instanceId");
                String currentTime = (String) result.get("currentTime");

                // 检查是否成功提取到所需信息
                if (instanceIdFromProvider != null && currentTime != null) {
                    // 构建输出字符串: "对应的1或者2或者3 + 当前时间"
                    simpleOutput.append("数字为").append(instanceIdFromProvider).append(" ，当前时间是 ").append(currentTime).append("。");
                } else {
                    // 如果缺少关键字段，返回包含原始 Map 的通用错误信息
                    simpleOutput.append("收到正常响应，但缺少必要数据 (instanceId 或 currentTime)。原始响应: ").append(result);
                }
            }
        } else {
            // 如果收到的 Map 为空或 null
            simpleOutput.append("未收到Provider数据或收到空响应。");
        }

        // 返回构建好的中文文本字符串作为 HTTP 响应体
        return simpleOutput.toString();
    }
}