package com.example.whatsaiassistcode.user;

import com.example.whatsaiassistcode.common.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.SpringDataJackson3Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        var objectMapper = JsonMapper.builder()
                .addModule(new SpringDataJackson3Configuration.PageModule(null))
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new ApiExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new JacksonJsonHttpMessageConverter(objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void findAllReturnsPagedUsers() throws Exception {
        when(userService.findAll(any())).thenReturn(new PageImpl<>(List.of(
                new UserResponse(1L, "Alice", "+5511999999999"),
                new UserResponse(2L, "Bob", "+5511888888888")
        )));

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void findByIdReturnsUser() throws Exception {
        when(userService.findById(7L)).thenReturn(new UserResponse(7L, "Carol", "+5511777777777"));

        mockMvc.perform(get("/users/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.name").value("Carol"))
                .andExpect(jsonPath("$.cellPhone").value("+5511777777777"));
    }

    @Test
    void findByIdReturnsNotFoundWhenServiceThrows() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found with id 99"));
    }

    @Test
    void createReturnsCreatedUser() throws Exception {
        when(userService.create(any())).thenReturn(new UserResponse(10L, "Diana", "+5511666666666"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Diana",
                                  "cellPhone": "+5511666666666"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Diana"))
                .andExpect(jsonPath("$.cellPhone").value("+5511666666666"));
    }

    @Test
    void createReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "cellPhone": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Name is required"))
                .andExpect(jsonPath("$.errors.cellPhone").value("Cell phone is required"));
    }

    @Test
    void updateReturnsUpdatedUser() throws Exception {
        when(userService.update(eq(5L), any())).thenReturn(new UserResponse(5L, "Eve", "+5511555555555"));

        mockMvc.perform(put("/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Eve",
                                  "cellPhone": "+5511555555555"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Eve"))
                .andExpect(jsonPath("$.cellPhone").value("+5511555555555"));
    }

    @Test
    void updateReturnsBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(put("/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "cellPhone": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Name is required"))
                .andExpect(jsonPath("$.errors.cellPhone").value("Cell phone is required"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(userService).delete(3L);

        mockMvc.perform(delete("/users/3"))
                .andExpect(status().isNoContent());

        verify(userService).delete(3L);
    }

    @Test
    void deleteReturnsNotFoundWhenServiceThrows() throws Exception {
        doThrow(new UserNotFoundException(88L)).when(userService).delete(88L);

        mockMvc.perform(delete("/users/88"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User not found with id 88"));
    }
}
