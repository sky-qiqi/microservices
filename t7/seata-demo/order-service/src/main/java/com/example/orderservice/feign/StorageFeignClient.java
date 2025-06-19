package com.example.orderservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "storage-service")
public interface StorageFeignClient {
    @PostMapping(value = "/storage/deduct", consumes = "application/json")
    String deduct(@RequestBody DeductRequest request);

    // DTO for JSON payload
    class DeductRequest {
        private String productId;
        private Integer amount;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }
    }
}