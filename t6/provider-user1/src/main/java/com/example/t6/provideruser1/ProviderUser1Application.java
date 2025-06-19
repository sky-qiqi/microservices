package com.example.t6.provideruser1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProviderUser1Application {

    public static void main(String[] args) {
        SpringApplication.run(ProviderUser1Application.class, args);
    }

    @GetMapping("/user/profile/{id}")
    public String getProfile(@PathVariable String id) {
        // Return a fixed identifier for this provider + the URL
        return "groupUSER1 [port 8084] + /user/profile/" + id;
    }
}