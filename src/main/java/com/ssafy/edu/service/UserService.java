package com.ssafy.edu.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssafy.edu.exception.DuplicateResourceException;
import com.ssafy.edu.exception.InvalidPasswordException;
import com.ssafy.edu.exception.ResourceNotFoundException;
import com.ssafy.edu.mapper.UserMapper;
import com.ssafy.edu.model.User;
import com.ssafy.edu.security.JwtTokenProvider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(user.getRole())
        );

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }

    @Transactional
    public User signup(User user) {
        validateSignupData(user);
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() == null ? "ROLE_USER" : user.getRole());
        userMapper.insert(user);
        
        user.setPassword(null); // 보안을 위해 비밀번호 제거
        return user;
    }

    private void validateSignupData(User user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (!StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
        if (!StringUtils.hasText(user.getName())) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            throw new DuplicateResourceException("이미 존재하는 사용자명입니다.");
        }
        if (userMapper.countByEmail(user.getEmail()) > 0) {
            throw new DuplicateResourceException("이미 등록된 이메일입니다.");
        }
    }

    @Transactional
    public Map<String, String> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(user.getRole())
        );

        String accessToken = jwtTokenProvider.createAccessToken(username, authorities);
        String refreshToken = jwtTokenProvider.createRefreshToken(username);

        // RefreshToken 저장
        Date expiryDate = new Date(System.currentTimeMillis() + 
            jwtTokenProvider.getRefreshTokenValidityInMilliseconds());
        userMapper.saveRefreshToken(user.getId(), refreshToken, expiryDate);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("tokenType", "Bearer");
        tokens.put("role", user.getRole());
        tokens.put("username", user.getUsername());
        return tokens;
    }

    @Transactional
    public void logout(Long userId) {
        userMapper.deleteRefreshToken(userId);
    }

    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidPasswordException("유효하지 않은 리프레시 토큰입니다.");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }
        
        String savedToken = userMapper.findRefreshTokenByUserId(user.getId());
        if (!refreshToken.equals(savedToken)) {
            throw new InvalidPasswordException("저장된 리프레시 토큰과 일치하지 않습니다.");
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(user.getRole())
        );

        String newAccessToken = jwtTokenProvider.createAccessToken(username, authorities);
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("tokenType", "Bearer");
        return tokens;
    }

    @Transactional(readOnly = true)
    public PageInfo<User> getUsers(int page, int size, String keyword) {
        PageHelper.startPage(page, size);
        List<User> users = userMapper.findAll(keyword);
        users.forEach(user -> user.setPassword(null)); // 보안을 위해 비밀번호 제거
        return new PageInfo<>(users);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }
        user.setPassword(null); // 보안을 위해 비밀번호 제거
        return user;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }
        user.setPassword(null);
        return user;
    }

    @Transactional
    public User updateUser(User user) {
        User existingUser = userMapper.findById(user.getId());
        if (existingUser == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }

        validateUpdateData(user, existingUser);

        // 비밀번호 처리
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null); // 비밀번호 변경하지 않음
        }

        userMapper.update(user);
        return findById(user.getId());
    }

    private void validateUpdateData(User user, User existingUser) {
        // 이메일 중복 체크 (자신의 이메일은 제외)
        if (StringUtils.hasText(user.getEmail()) && 
            !user.getEmail().equals(existingUser.getEmail()) && 
            userMapper.countByEmail(user.getEmail()) > 0) {
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 유효성 검사
        if (StringUtils.hasText(user.getPassword()) && user.getPassword().length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자 이상이어야 합니다.");
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 리프레시 토큰 삭제
        userMapper.deleteRefreshToken(id);
        // 사용자 삭제
        userMapper.delete(id);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 유효성 검사
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new IllegalArgumentException("새 비밀번호는 6자 이상이어야 합니다.");
        }

        // 비밀번호 변경
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);
    }

    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            throw new IllegalArgumentException("새 비밀번호는 6자 이상이어야 합니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.update(user);

        // 기존 리프레시 토큰 삭제
        userMapper.deleteRefreshToken(userId);
    }

    @Transactional
    public boolean checkEmailAvailability(String email) {
        return userMapper.countByEmail(email) == 0;
    }

    @Transactional
    public boolean checkUsernameAvailability(String username) {
        return userMapper.countByUsername(username) == 0;
    }
}