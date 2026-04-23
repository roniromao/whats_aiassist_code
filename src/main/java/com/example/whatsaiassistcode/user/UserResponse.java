package com.example.whatsaiassistcode.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String cellPhone;

    public static UserResponse fromEntity(User user) {
        var response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}
