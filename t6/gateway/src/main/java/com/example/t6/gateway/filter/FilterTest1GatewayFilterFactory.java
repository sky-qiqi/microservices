package com.example.t6.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;

// This filter needs CachedRequestBodyGatewayFilter to run before it
@Component
public class FilterTest1GatewayFilterFactory extends ModifyRequestBodyGatewayFilterFactory<FilterTest1GatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(FilterTest1GatewayFilterFactory.class);
    private static final String MY_DATA_FIELD = "mydata";

    // Inject ServerCodecConfigurer and pass config class
    public FilterTest1GatewayFilterFactory(ServerCodecConfigurer codecConfigurer) {
        super(Config.class, codecConfigurer);
    }

    @Override
    public String name() {
        return "FilterTest1";
    }

    // Configuration class for the filter
    public static class Config {
        private String key;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    @Override
    protected JsonNode modifyBody(JsonNode originalBody, Config config) {
        // Ensure the key is configured
        if (config == null || config.getKey() == null || config.getKey().isEmpty()) {
            log.error("FilterTest1: AES key is not configured.");
            return originalBody; // Continue without modification
        }

        if (originalBody instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) originalBody;
            if (objectNode.has(MY_DATA_FIELD) && objectNode.get(MY_DATA_FIELD).isTextual()) {
                String originalMyData = objectNode.get(MY_DATA_FIELD).asText();
                log.info("FilterTest1: Encrypting '{}' for field '{}'", originalMyData, MY_DATA_FIELD);
                try {
                    String encryptedMyData = AesUtils.encrypt(originalMyData, config.getKey());
                    log.info("FilterTest1: Encrypted to '{}'", encryptedMyData);
                    // Update the JSON node with the encrypted value
                    objectNode.put(MY_DATA_FIELD, encryptedMyData);
                } catch (Exception e) {
                    log.error("FilterTest1: AES encryption failed for field '{}'", MY_DATA_FIELD, e);
                    // Return original body on error
                    return originalBody;
                }
            } else {
                log.warn("FilterTest1: Field '{}' not found or not text in request body. Skipping encryption.", MY_DATA_FIELD);
            }
        } else {
            log.warn("FilterTest1: Request body is not a JSON object. Skipping encryption.");
        }
        return originalBody; // Return the modified (or original) body
    }
}