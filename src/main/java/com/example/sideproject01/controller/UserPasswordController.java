package com.example.sideproject01.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sideproject01.dto.PasswordChangeRequestDto;
import com.example.sideproject01.service.UserPasswordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/me")
public class UserPasswordController {

    private final UserPasswordService userPasswordService;

    /**
     * ✅ PATCH /v1/users/me/password
     * - 인증 필요(토큰 필요)
     * - Principal에서 userName을 얻어서 본인 비밀번호 변경
     * - 성공하면 204(No Content)
     */
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(
            Principal principal,
            @RequestBody PasswordChangeRequestDto dto
    ) {
        // principal.getName() == JWT subject(네 코드 기준 authentication.getName())
        userPasswordService.changeMyPassword(principal.getName(), dto);
        return ResponseEntity.noContent().build();
    }
}
