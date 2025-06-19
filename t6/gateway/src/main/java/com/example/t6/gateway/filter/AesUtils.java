package com.example.t6.gateway.filter;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // Simple mode for example

    public static String encrypt(String data, String key) throws Exception {
        if (key.getBytes(StandardCharsets.UTF_8).length != 16 && key.getBytes(StandardCharsets.UTF_8).length != 24 && key.getBytes(StandardCharsets.UTF_8).length != 32) {
            throw new IllegalArgumentException("AES key size must be 16, 24, or 32 bytes.");
        }
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String data, String key) throws Exception {
        if (key.getBytes(StandardCharsets.UTF_8).length != 16 && key.getBytes(StandardCharsets.UTF_8).length != 24 && key.getBytes(StandardCharsets.UTF_8).length != 32) {
            throw new IllegalArgumentException("AES key size must be 16, 24, or 32 bytes.");
        }
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(data);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}