package com.ssafy.edu.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // getter, setter, toString, equals, hashCode 메서드 생성
@Builder            // 빌더 패턴 구현
@NoArgsConstructor  // 매개변수 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 매개변수로 받는 생성자
@Schema(description = "로그아웃 요청")
public class LogoutRequest {
    
    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "사용자 ID는 필수입니다.") // Long 타입에는 @NotNull 사용
    private Long userId;
}