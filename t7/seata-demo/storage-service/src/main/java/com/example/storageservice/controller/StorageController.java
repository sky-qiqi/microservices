package com.example.storageservice.controller;

import com.example.storageservice.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("/deduct")
    public String deduct(@RequestBody DeductRequest request) {
        return storageService.decreaseStorage(request.getProductId(), request.getAmount());
    }

    // DTO class for JSON payload
    public static class DeductRequest {
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