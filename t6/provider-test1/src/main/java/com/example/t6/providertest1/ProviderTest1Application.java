package com.example.t6.providertest1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType; // 导入 MediaType
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@SpringBootApplication
@RestController
public class ProviderTest1Application {

    private static final Logger log = LoggerFactory.getLogger(ProviderTest1Application.class);
    // WebClient to call Gateway (assuming gateway is on 8080)
    // Note: Consider using @LoadBalanced WebClient if calling other microservices,
    // but for calling the Gateway itself, a direct WebClient is fine.
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

    public static void main(String[] args) {
        SpringApplication.run(ProviderTest1Application.class, args);
    }

    // Endpoint for URL Rewriting test (external /test1 maps to this internal /x/y/test1)
    // This is hit after the gateway's RewritePath filter.
    @GetMapping("/x/y/test1")
    public String rewriteTest1() {
        // Return a fixed identifier for this provider + the URL
        return "URL Rewriting Test: Accessed internal /x/y/test1 on port 8086";
    }


    // 5, Endpoint for Filter Test 1 (Receives body, gateway's FilterTest1 encrypts mydata field)
    // Then this service makes an internal call back to the gateway's /filtertest2 endpoint.
    @PostMapping("/filtertest1")
    public Mono<String> handleFilterTest1(@RequestBody JsonNode requestBody) {
        // Return a fixed identifier for this provider
        String providerInfo = "Provider-test1 [port 8086]";

        log.info("{} received body: {}", providerInfo, requestBody.toString());

        // Extract the encrypted mydata from the received requestBody
        final String extractedEncryptedMyData; // 声明一个新的最终变量
        if (requestBody != null && requestBody.has("mydata") && requestBody.get("mydata").isTextual()) {
            extractedEncryptedMyData = requestBody.get("mydata").asText(); // 在这里赋值
            log.info("{} extracted encrypted mydata: {}", providerInfo, extractedEncryptedMyData);
        } else {
            extractedEncryptedMyData = "N/A"; // 在 else 分支也赋值，确保变量总是被赋值一次
            log.warn("{} 'mydata' field not found or not textual in received body. Using default: {}", providerInfo, extractedEncryptedMyData);
        }


        // The gateway's FilterTest1 filter should have ALREADY encrypted the 'mydata' field in requestBody.
        // We now forward this (potentially encrypted) body to the gateway's /filtertest2 endpoint.
        String bodyToSend = requestBody.toString(); // Send the body as received (with encrypted mydata)

        log.info("{} calling gateway /filtertest2 with body: {}", providerInfo, bodyToSend);

        // Use WebClient to call the gateway
        return webClient.post()
                .uri("/filtertest2") // Call the gateway endpoint which maps to provider-test2 with FilterTest2
                // ADD THIS LINE TO SET CONTENT-TYPE
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE) // Explicitly set Content-Type to application/json
                .bodyValue(bodyToSend) // Send the body received from the original request
                .retrieve()
                .bodyToMono(String.class) // Expect a String response
                .map(response -> {
                    // 现在可以引用 extractedEncryptedMyData，因为它是一个实际上的最终变量
                    return providerInfo + " received encrypted mydata: " + extractedEncryptedMyData +
                            ", called /filtertest2 via gateway, response: " + response;
                })
                .onErrorResume(e -> {
                    log.error("{} Error calling gateway /filtertest2", providerInfo, e);
                    return Mono.just(providerInfo + " Error calling /filtertest2: " + e.getMessage());
                });
    }
}