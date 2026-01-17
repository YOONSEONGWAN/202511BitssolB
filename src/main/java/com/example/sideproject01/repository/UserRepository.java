package com.example.sideproject01.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);
    
    boolean existsByUserName(String userName);

    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // ✅ 본인(id) 제외 중복체크 (derived query라 @Query 필요 없음)
    boolean existsByNicknameAndIdNot(String nickname, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);
}
