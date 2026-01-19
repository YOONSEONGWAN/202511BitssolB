package com.example.sideproject01.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users") // 실제 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
	@SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
	@Column(name = "id")
	private Long id;

	@Column(unique = true, nullable = false)
	private String userName;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private UserRole role;

	// User.java (추가)
	@Column(name = "profile_image_url", length = 300)
	private String profileImageUrl;
	
	// 추가 -재원
	@Column(name = "nickname", length = 30, unique = true)
	private String nickname;

	@Column(name = "email", length = 100, unique = true)
	private String email;

	@Column(name = "phone", length = 20, unique = true)
	private String phone;

	// 추가적으로 email, role 등 필요한 필드를 여기에 추가할 수 있습니다.
	// 예: private String email;
	// 예: private String role;
}