package com.server.server;


import java.security.MessageDigest;

import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private static final String SECRET_KEY = "iotSharedKey123";

    public String computeChecksum(int seq, String payload) {
        try {
            String data = seq + payload + SECRET_KEY;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
