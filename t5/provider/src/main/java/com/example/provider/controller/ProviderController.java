package com.example.provider.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/provider")
public class ProviderController {

    // 注入当前服务的端口号
    @Value("${server.port}")
    private String serverPort;

    // DateTimeFormatter 用于格式化时间
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @GetMapping("/data/{id}")
    public Map<String, Object> getData(@PathVariable("id") int id) {

        // 根据端口号计算逻辑 instanceId
        int instanceLogicalId = 0;
        try {
            int portNum = Integer.parseInt(serverPort);
            // 假设你的 Provider 实例运行在端口 8081, 8082, 8083
            if (portNum >= 8081 && portNum <= 8083) {
                instanceLogicalId = portNum % 10; // 端口尾数作为逻辑 ID (1, 2, 或 3)
            } else {
                // 处理其他端口的情况，例如设置为端口号本身或一个默认值
                instanceLogicalId = portNum; // 如果端口不是 8081-8083 范围，就直接使用端口号
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing server port: " + serverPort + ", setting instanceId to 0");
            instanceLogicalId = 0; // 解析失败时设置为 0 或其他值
        }


        System.out.println("Provider instance (Logical ID: " + instanceLogicalId + ", Port: " + serverPort + ") received request for id: " + id);

        Map<String, Object> response = new HashMap<>();

        if (id == 3) {
            System.out.println("ID is 3, throwing exception from instance (Logical ID: " + instanceLogicalId + ", Port: " + serverPort + ")");
            // Simulate an error condition
            throw new RuntimeException("Error occurred for ID 3 on instance (Logical ID: " + instanceLogicalId + ", Port: " + serverPort + ")");
        } else {
            // 如果 id 不等于 3，返回正常数据
            response.put("currentTime", LocalTime.now().format(formatter));
            response.put("instanceId", instanceLogicalId); // 现在 instanceId 会根据端口变化
            response.put("port", serverPort); // 返回实际端口号
            response.put("message", "Data from provider instance " + instanceLogicalId + " on port " + serverPort); // 消息中也反映逻辑 ID

            System.out.println("Returning data from instance (Logical ID: " + instanceLogicalId + ", Port: " + serverPort + ") for id: " + id);
        }

        return response;
    }
}