package com.example.sideproject01.service;

import com.example.sideproject01.dto.PasswordChangeRequestDto;

public interface UserPasswordService {

    /**
     * 로그인한 사용자 본인 비밀번호 변경
     * @param userName principal.getName() (JWT subject)
     * @param dto currentPassword / newPassword
     */
    void changeMyPassword(String userName, PasswordChangeRequestDto dto);
}
