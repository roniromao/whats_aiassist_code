package com.example.whatsaiassistcode.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must have at most 120 characters")
    private String name;

    @NotBlank(message = "Cell phone is required")
    @Size(max = 20, message = "Cell phone must have at most 20 characters")
    private String cellPhone;
}
