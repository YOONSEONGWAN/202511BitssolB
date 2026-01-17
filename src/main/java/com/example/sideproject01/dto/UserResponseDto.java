package com.example.sideproject01.dto;


import com.example.sideproject01.entity.User;
import com.example.sideproject01.entity.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 빌더 패턴 사용
public class UserResponseDto {

	 private String name; // userName
	  private String nickname;
	  private String email;
	  private String phone;
	  private String profileImageUrl;

	  public static UserResponseDto from(User user) {
	    return UserResponseDto.builder()
	        .name(user.getUserName())
	        .nickname(user.getNickname())
	        .email(user.getEmail())
	        .phone(user.getPhone())
	        .profileImageUrl(user.getProfileImageUrl())
	        .build();
	  }
}