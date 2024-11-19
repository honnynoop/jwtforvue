package com.ssafy.edu.mapper;

import com.ssafy.edu.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper {
    // 기본 CRUD
    void insert(User user);
    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll(@Param("keyword") String keyword);
    void update(User user);
    void delete(Long id);
    
    // 카운트 관련
    int countByUsername(String username);
    int countByEmail(String email);
    
    // RefreshToken 관련
    void saveRefreshToken(@Param("userId") Long userId, 
                         @Param("token") String token, 
                         @Param("expiryDate") Date expiryDate);
    String findRefreshTokenByUserId(Long userId);
    void deleteRefreshToken(Long userId);
    void deleteExpiredRefreshTokens();
    
    // 권한 관련
    void updateRole(@Param("userId") Long userId, @Param("role") String role);
    List<User> findByRole(String role);
    
    // 로그인 관련
    void updateLastLoginDate(@Param("userId") Long userId);
    void incrementLoginAttempts(String username);
    void resetLoginAttempts(String username);
    int getLoginAttempts(String username);
    void lockAccount(String username);
    void unlockAccount(String username);
    
    // 비밀번호 관련
    void updatePassword(@Param("userId") Long userId, @Param("password") String password);
    void createPasswordResetToken(@Param("userId") Long userId, 
                                @Param("token") String token, 
                                @Param("expiryDate") Date expiryDate);
    User findByPasswordResetToken(String token);
    void deletePasswordResetToken(String token);
    
    // 검색 및 필터링
    List<User> findByCreatedAtBetween(@Param("startDate") Date startDate, 
                                     @Param("endDate") Date endDate);
    List<User> findByKeywordAndRole(@Param("keyword") String keyword, 
                                   @Param("role") String role);
    
    // 통계
    int countTotalUsers();
    int countUsersByRole(String role);
    int countActiveUsers();
}