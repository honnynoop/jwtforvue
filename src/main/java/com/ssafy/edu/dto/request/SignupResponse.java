package com.ssafy.edu.dto.request;

import com.ssafy.edu.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class SignupResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String role;
    private String createdAt;

    public static SignupResponse from(User user) {
        SignupResponse response = new SignupResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}