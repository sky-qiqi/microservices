package com.example.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

    @RestController
    @RefreshScope
    public class ConsumerController {
        @Value("${a}")
        private String consumerA;

        @GetMapping("/combineA")
        public String combineA() {
            String providerA = new RestTemplate().getForObject("http://localhost:8080/getA", String.class);
            return "Consumer A: " + consumerA + " + Provider A: " + providerA;
        }
    }