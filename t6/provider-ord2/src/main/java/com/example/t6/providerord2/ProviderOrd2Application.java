package com.example.t6.providerord2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProviderOrd2Application {

    public static void main(String[] args) {
        SpringApplication.run(ProviderOrd2Application.class, args);
    }

    // This endpoint is specifically targeted by the /ordspecial route in the gateway
    @GetMapping("/ordspecial")
    public String getSpecialItem() {
        // Return a fixed identifier for this provider + the URL
        return "groupORD2 [port 8083] + /ordspecial (Special)";
    }

    // Also include the standard order route for weighted load balancing
    @GetMapping("/ord/item/{id}")
    public String getItem(@PathVariable String id) {
        // Return a fixed identifier for this provider + the URL
        return "groupORD2 [port 8083] + /ord/item/" + id;
    }
}