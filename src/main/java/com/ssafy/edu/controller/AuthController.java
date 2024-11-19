package com.ssafy.edu.controller;

import com.ssafy.edu.dto.request.DuplicationCheckResponse;
import com.ssafy.edu.dto.request.LoginRequest;
import com.ssafy.edu.dto.request.LoginResponse;
import com.ssafy.edu.dto.request.LogoutRequest;
import com.ssafy.edu.dto.request.PasswordChangeRequest;
import com.ssafy.edu.dto.request.SignupRequest;
import com.ssafy.edu.dto.request.SignupResponse;
import com.ssafy.edu.dto.request.TokenRefreshRequest;
import com.ssafy.edu.dto.request.TokenRefreshResponse;
import com.ssafy.edu.exception.TokenRefreshException;
import com.ssafy.edu.model.User;
import com.ssafy.edu.security.JwtTokenProvider;
import com.ssafy.edu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        User user = request.toUser();
        User savedUser = userService.signup(user);
        return ResponseEntity.ok(SignupResponse.from(savedUser));
    }
/*
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW1lcyIsImlhdCI6MTczMTQ3NzUyOCwiZXhwIjoxNzMxNDgxMTI4LCJhdXRoIjoiUk9MRV9VU0VSIn0.2ZLQ6ccHsK5TuesSjR_X16THN6uO4zuv3jM0xfBvmyw",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW1lcyIsImlhdCI6MTczMTQ3NzUyOCwiZXhwIjoxNzMyMDgyMzI4fQ.BHs8xkOkLrDjStFhXjLnl3aVubA8DgQoKnJFiLQZUa0",
  "tokenType": "Bearer",
  "role": "ROLE_USER",
  "username": "james"
}
 */
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Map<String, String> tokens = userService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(tokens));
    }

    @Operation(summary = "로그아웃", description = "사용자의 리프레시 토큰을 제거합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
    	log.debug("------------------------------------------>{}",request);
        userService.logout(request.getUserId());
        return ResponseEntity.ok().build();
    }

    @Operation(
    	    summary = "토큰 갱신",
    	    description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다."
    	)
    	@PostMapping("/refresh")
    	public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
    	    try {
    	        String refreshToken = request.getRefreshToken();
    	        Map<String, String> tokens = userService.refreshToken(refreshToken);
    	        
    	        return ResponseEntity.ok(new TokenRefreshResponse(
    	            tokens.get("accessToken"),
    	            jwtTokenProvider.getAccessTokenValidityInMilliseconds()/ 1000
    	        ));
    	    } catch (TokenRefreshException e) {
    	        throw new TokenRefreshException(request.getRefreshToken(), "리프레시 토큰이 유효하지 않습니다.");
    	    }
    	}

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새로운 비밀번호로 변경합니다.")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(request.getUserId(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 주소의 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-email")
    public ResponseEntity<DuplicationCheckResponse> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = userService.checkEmailAvailability(email);
        return ResponseEntity.ok(new DuplicationCheckResponse(isAvailable));
    }

    @Operation(summary = "사용자명 중복 확인", description = "사용자명의 사용 가능 여부를 확인합니다.")
    @GetMapping("/check-username")
    public ResponseEntity<DuplicationCheckResponse> checkUsernameAvailability(@RequestParam String username) {
        boolean isAvailable = userService.checkUsernameAvailability(username);
        return ResponseEntity.ok(new DuplicationCheckResponse(isAvailable));
    }
}
