package com.example.whatsaiassistcode.whatsapp;

import jakarta.validation.constraints.NotBlank;

public record SendTextMessageRequest(
        @NotBlank(message = "Destination number is required") String to,
        @NotBlank(message = "Message text is required") String text
) {
}

