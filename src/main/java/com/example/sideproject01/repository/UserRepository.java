package com.example.sideproject01.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String username);

	long countByUserName(String userName);

	long countByNickname(String nickname);

	long countByEmail(String email);

	long countByPhone(String phone);

	long countByNicknameAndIdNot(String nickname, Long id);

	long countByEmailAndIdNot(String email, Long id);

	long countByPhoneAndIdNot(String phone, Long id);
}
