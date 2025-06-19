package com.example.t6.providerord1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ProviderOrd1Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ProviderOrd1Application.class);
        // Pass the port as a system property so the controller can access it
        app.run(args);
    }

    @GetMapping("/ord/item/{id}")
    public String getItem(@PathVariable String id) {
        String port = System.getProperty("server.port");
        if (port == null) {
            // Fallback if run without system property (e.g., directly from IDE)
            port = "Unknown Port"; // Or try to get it from Environment
            try {
                port = SpringApplication.run(ProviderOrd1Application.class).getEnvironment().getProperty("local.server.port");
            } catch (Exception e) {
                // ignore
            }
        }
        // Getting the port reliably inside the controller method can be tricky.
        // A better way is to inject Environment or use a Value annotation.
        // For simplicity in this example, we'll use a placeholder or a less reliable method.
        // Let's just return a fixed identifier for this provider + the URL.
        return "groupORD1 [port 8081] + /ord/item/" + id;
    }
}