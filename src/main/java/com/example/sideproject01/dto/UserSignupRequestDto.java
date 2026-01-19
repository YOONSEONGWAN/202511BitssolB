package com.example.sideproject01.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 (JSON 파싱할 때 필수)
public class UserSignupRequestDto {

    private String password;
    private String name;  // 기존: userName
    private String role; // "USER" 또는 "ADMIN" (String으로 받아서 변환할 예정)
    
    // ✅ 추가
    private String nickname;
    private String email;
    private String phone;
    
    
}