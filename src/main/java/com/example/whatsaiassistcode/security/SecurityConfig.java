package com.example.whatsaiassistcode.security;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties(ApiSecurityProperties.class)
public class SecurityConfig {

    private static final String ROLE_READER = "READER";
    private static final String ROLE_WRITER = "WRITER";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole(ROLE_READER, ROLE_WRITER)
                        .requestMatchers(HttpMethod.POST, "/users/**").hasRole(ROLE_WRITER)
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole(ROLE_WRITER)
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(ROLE_WRITER)
                .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, HttpStatus.UNAUTHORIZED,
                                        "Authentication required",
                                        "Provide valid HTTP Basic credentials to access this API."))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, HttpStatus.FORBIDDEN,
                                        "Access denied",
                                        "Your user does not have permission to access this resource.")))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(ApiSecurityProperties properties, PasswordEncoder passwordEncoder) {
        var reader = User.withUsername(properties.getReader().getUsername())
                .password(passwordEncoder.encode(properties.getReader().getPassword()))
                .roles(ROLE_READER)
                .build();

        var writer = User.withUsername(properties.getWriter().getUsername())
                .password(passwordEncoder.encode(properties.getWriter().getPassword()))
                .roles(ROLE_WRITER)
                .build();

        return new InMemoryUserDetailsManager(reader, writer);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static void writeErrorResponse(
            jakarta.servlet.http.HttpServletResponse response,
            HttpStatus status,
            String error,
            String message
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var body = """
                {"status":%d,"error":"%s","message":"%s"}
                """.formatted(status.value(), escapeJson(error), escapeJson(message)).trim();
        response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
