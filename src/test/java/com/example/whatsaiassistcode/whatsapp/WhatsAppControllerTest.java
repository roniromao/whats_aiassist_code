package com.example.whatsaiassistcode.whatsapp;

import com.example.whatsaiassistcode.common.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WhatsAppControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WhatsAppService whatsAppService;

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        var props = new WhatsAppProperties();
        props.setVerifyToken("verify123");

        mockMvc = MockMvcBuilders.standaloneSetup(new WhatsAppController(whatsAppService, props))
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new JacksonJsonHttpMessageConverter(new ObjectMapper()))
                .setValidator(validator)
                .build();
    }

    @Test
    void verifyWebhookReturnsChallengeWhenTokenMatches() throws Exception {
        mockMvc.perform(get("/whatsapp/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "verify123")
                        .param("hub.challenge", "abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("abc123"));
    }

    @Test
    void verifyWebhookReturnsForbiddenWhenTokenIsInvalid() throws Exception {
        mockMvc.perform(get("/whatsapp/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.verify_token", "bad-token")
                        .param("hub.challenge", "abc123"))
                .andExpect(status().isForbidden());
    }

    @Test
    void sendTextMessageReturnsWhatsAppResponse() throws Exception {
        when(whatsAppService.sendTextMessage(any())).thenReturn(Map.of(
                "messages", List.of(Map.of("id", "wamid.test"))
        ));

        mockMvc.perform(post("/whatsapp/messages/text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "to": "5511999999999",
                                  "text": "hello"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].id").value("wamid.test"));
    }

    @Test
    void sendTextMessageReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/whatsapp/messages/text")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "to": "",
                                  "text": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.errors.to").value("Destination number is required"))
                .andExpect(jsonPath("$.errors.text").value("Message text is required"));
    }
}

