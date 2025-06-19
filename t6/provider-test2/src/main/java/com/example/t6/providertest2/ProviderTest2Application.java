package com.example.t6.providertest2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType; // 导入 MediaType
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class ProviderTest2Application {

    private static final Logger log = LoggerFactory.getLogger(ProviderTest2Application.class);

    public static void main(String[] args) {
        SpringApplication.run(ProviderTest2Application.class, args);
    }

    // 5, Endpoint for Filter Test 2 (Receives body, gateway's FilterTest2 decrypts mydata field)
    // This is hit after the gateway's FilterTest2 filter runs.
    // Explicitly set consumes to application/json
    @PostMapping(value = "/filtertest2", consumes = MediaType.APPLICATION_JSON_VALUE) // ADD consumes attribute
    public String handleFilterTest2(@RequestBody JsonNode requestBody) {
        // Return a fixed identifier for this provider
        String providerInfo = "Provider-test2 [port 8087]";

        log.info("{} received body: {}", providerInfo, requestBody.toString());

        // The gateway's FilterTest2 filter should have ALREADY decrypted the 'mydata' field
        // Now we extract and print the value.
        String myDataValue = "N/A";
        // Check if the received body is indeed a JSON object and has the 'mydata' field
        // Added check for isObject() for robustness
        if (requestBody.isObject() && requestBody.has("mydata") && requestBody.get("mydata").isTextual()) {
            myDataValue = requestBody.get("mydata").asText();
            log.info("{} - Value of 'mydata' field (after decryption by gateway): {}", providerInfo, myDataValue);
        } else {
            log.warn("{} - Received body is not a JSON object or 'mydata' field not found/not text. Received: {}", providerInfo, requestBody.toString());
        }


        // Return a response indicating what was received (should be the decrypted value)
        return providerInfo + " received mydata: " + myDataValue;
    }
}