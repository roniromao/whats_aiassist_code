package com.example.whatsaiassistcode.whatsapp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.whatsapp")
public class WhatsAppProperties {

    private String apiVersion = "v23.0";
    private String phoneNumberId;
    private String accessToken;
    private String verifyToken;
}

