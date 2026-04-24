package com.example.whatsaiassistcode.security;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app.security")
public class ApiSecurityProperties {

    private final Credentials reader = new Credentials();
    private final Credentials writer = new Credentials();

    @Data
    public static class Credentials {
        private String username;
        private String password;
    }
}
