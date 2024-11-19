package com.ssafy.edu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보")
public class User {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자명", example = "user1")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "비밀번호", example = "password123")
    private String password;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "이메일", example = "user1@example.com")
    private String email;

    @Schema(description = "역할", example = "ROLE_USER")
    private String role;

    @Schema(description = "계정 활성화 여부", example = "true")
    private boolean enabled;

    @Schema(description = "계정 잠금 여부", example = "true")
    private boolean accountNonLocked;

    @Schema(description = "로그인 시도 횟수", example = "0")
    private int loginAttempts;

    @Schema(description = "마지막 로그인 일시", example = "2024-03-14 12:00:00")
    private String lastLoginDate;

    @Schema(description = "생성 일시", example = "2024-03-14 12:00:00")
    private String createdAt;

    @Schema(description = "수정 일시", example = "2024-03-14 12:00:00")
    private String updatedAt;

    // 보안 관련 메서드
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    // 편의 메서드
    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }

    public void lock() {
        this.accountNonLocked = false;
    }

    public void unlock() {
        this.accountNonLocked = true;
        this.loginAttempts = 0;
    }

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(this.role);
    }

    // DTO 변환 메서드
    public UserDto toDto() {
        return UserDto.builder()
                .id(id)
                .username(username)
                .name(name)
                .email(email)
                .role(role)
                .enabled(enabled)
                .accountNonLocked(accountNonLocked)
                .lastLoginDate(lastLoginDate)
                .createdAt(createdAt)
                .build();
    }

    // 내부 DTO 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String username;
        private String name;
        private String email;
        private String role;
        private boolean enabled;
        private boolean accountNonLocked;
        private String lastLoginDate;
        private String createdAt;
    }

    // 비밀번호 업데이트용 정적 팩토리 메서드
    public static User createWithPassword(Long id, String password) {
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        return user;
    }
    
    // 계정 상태 확인용 메서드
    @JsonIgnore
    public boolean isValid() {
        return this.enabled && this.accountNonLocked;
    }

    // 권한 확인용 메서드
    public boolean hasRole(String roleToCheck) {
        return this.role != null && this.role.equals(roleToCheck);
    }
}