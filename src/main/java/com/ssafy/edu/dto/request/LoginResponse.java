package com.ssafy.edu.dto.request;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
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
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String role;
    private String username;

    public LoginResponse(Map<String, String> tokens) {
        this.accessToken = tokens.get("accessToken");
        this.refreshToken = tokens.get("refreshToken");
        this.tokenType = tokens.get("tokenType");
        this.role = tokens.get("role");
        this.username = tokens.get("username");
    }
}