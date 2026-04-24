package com.example.whatsaiassistcode.whatsapp;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/whatsapp")
public class WhatsAppController {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppController.class);
    private final WhatsAppService whatsAppService;
    private final WhatsAppProperties properties;

    public WhatsAppController(WhatsAppService whatsAppService, WhatsAppProperties properties) {
        this.whatsAppService = whatsAppService;
        this.properties = properties;
    }

    @GetMapping(value = "/webhook", produces = MediaType.TEXT_PLAIN_VALUE)
    public String verifyWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.verify_token") String verifyToken,
            @RequestParam(name = "hub.challenge") String challenge
    ) {
        if (!"subscribe".equals(mode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hub.mode");
        }

        if (!Objects.equals(properties.getVerifyToken(), verifyToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid verify token");
        }
        return challenge;
    }

    @PostMapping("/webhook")
    @ResponseStatus(HttpStatus.OK)
    public String receiveWebhook(@RequestBody(required = false) Map<String, Object> payload) {
        log.info("WhatsApp webhook payload received: {}", payload);
        return "EVENT_RECEIVED";
    }

    @PostMapping("/messages/text")
    public Map sendTextMessage(@Valid @RequestBody SendTextMessageRequest request) {
        return whatsAppService.sendTextMessage(request);
    }
}

