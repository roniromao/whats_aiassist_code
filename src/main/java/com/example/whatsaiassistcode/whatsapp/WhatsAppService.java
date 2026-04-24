package com.example.whatsaiassistcode.whatsapp;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class WhatsAppService {

    private final RestClient restClient;
    private final WhatsAppProperties properties;

    public WhatsAppService(RestClient.Builder restClientBuilder, WhatsAppProperties properties) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl("https://graph.facebook.com")
                .build();
    }

    public Map sendTextMessage(SendTextMessageRequest request) {
        validateConfiguration();
        var path = "/%s/%s/messages".formatted(properties.getApiVersion(), properties.getPhoneNumberId());

        var payload = Map.of(
                "messaging_product", "whatsapp",
                "to", request.to(),
                "type", "text",
                "text", Map.of(
                        "preview_url", false,
                        "body", request.text()
                )
        );

        try {
            return restClient.post()
                    .uri(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getAccessToken())
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientResponseException ex) {
            var message = "WhatsApp API returned %d: %s".formatted(ex.getStatusCode().value(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(BAD_GATEWAY, message, ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to send WhatsApp message", ex);
        }
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getPhoneNumberId()) || !StringUtils.hasText(properties.getAccessToken())) {
            throw new ResponseStatusException(
                    INTERNAL_SERVER_ERROR,
                    "WhatsApp configuration is missing. Set app.whatsapp.phone-number-id and app.whatsapp.access-token."
            );
        }
    }
}
