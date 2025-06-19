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
public class FilterTest2GatewayFilterFactory extends ModifyRequestBodyGatewayFilterFactory<FilterTest2GatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(FilterTest2GatewayFilterFactory.class);
    private static final String MY_DATA_FIELD = "mydata";

    // Inject ServerCodecConfigurer and pass config class
    public FilterTest2GatewayFilterFactory(ServerCodecConfigurer codecConfigurer) {
        super(Config.class, codecConfigurer);
    }

    @Override
    public String name() {
        return "FilterTest2";
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
            log.error("FilterTest2: AES key is not configured.");
            return originalBody; // Continue without modification
        }

        if (originalBody instanceof ObjectNode) {
            ObjectNode objectNode = (ObjectNode) originalBody;
            if (objectNode.has(MY_DATA_FIELD) && objectNode.get(MY_DATA_FIELD).isTextual()) {
                String encryptedMyData = objectNode.get(MY_DATA_FIELD).asText();
                log.info("FilterTest2: Decrypting '{}' for field '{}'", encryptedMyData, MY_DATA_FIELD);
                try {
                    String decryptedMyData = AesUtils.decrypt(encryptedMyData, config.getKey());
                    log.info("FilterTest2: Decrypted to '{}'", decryptedMyData);
                    // Update the JSON node with the decrypted value
                    objectNode.put(MY_DATA_FIELD, decryptedMyData);
                } catch (Exception e) {
                    log.error("FilterTest2: AES decryption failed for field '{}'. Input was: {}", MY_DATA_FIELD, encryptedMyData, e);
                    // Return original body on error
                    return originalBody;
                }
            } else {
                log.warn("FilterTest2: Field '{}' not found or not text in request body. Skipping decryption.", MY_DATA_FIELD);
            }
        } else {
            log.warn("FilterTest2: Request body is not a JSON object. Skipping decryption.", MY_DATA_FIELD);
        }
        return originalBody; // Return the modified (or original) body
    }
}