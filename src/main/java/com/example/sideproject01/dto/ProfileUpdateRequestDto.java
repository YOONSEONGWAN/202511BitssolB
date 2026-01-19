package com.example.sideproject01.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateRequestDto {
    private String nickname;
    private String email;
    private String phone;
}
