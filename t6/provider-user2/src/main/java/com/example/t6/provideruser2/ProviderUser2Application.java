package com.example.t6.provideruser2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProviderUser2Application {

    public static void main(String[] args) {
        SpringApplication.run(ProviderUser2Application.class, args);
    }

    @GetMapping("/user/profile/{id}")
    public String getProfile(@PathVariable String id) {
        // Return a fixed identifier for this provider + the URL
        return "groupUSER2 [port 8085] + /user/profile/" + id;
    }
}