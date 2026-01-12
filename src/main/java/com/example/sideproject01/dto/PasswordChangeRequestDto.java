package com.example.sideproject01.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호 변경 요청 DTO
 * - currentPassword: 현재 비밀번호(검증용)
 * - newPassword: 새 비밀번호(저장용)
 */
@Getter
@Setter
public class PasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
}
