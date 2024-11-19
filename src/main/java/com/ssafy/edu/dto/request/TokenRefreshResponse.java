package com.ssafy.edu.dto.request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 갱신 응답")
public class TokenRefreshResponse {
    
    @Schema(description = "새로 발급된 액세스 토큰")
    private String accessToken;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "액세스 토큰 만료 시간 (초)", example = "3600")
    private long expiresIn;

    public TokenRefreshResponse(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}