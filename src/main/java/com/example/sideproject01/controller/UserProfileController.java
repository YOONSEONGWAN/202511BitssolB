package com.example.sideproject01.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sideproject01.dto.ProfileUpdateRequestDto;
import com.example.sideproject01.dto.UserResponseDto;
import com.example.sideproject01.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/me")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * ✅ PATCH /v1/users/me/profile
     * nickname/email/phone 중 들어온 값만 변경 + 중복 확인
     */
    @PatchMapping("/profile")
    public ResponseEntity<UserResponseDto> updateMyProfile(
            Principal principal,
            @RequestBody ProfileUpdateRequestDto dto
    ) {
        UserResponseDto updated = userProfileService.updateMyProfile(principal.getName(), dto);
        return ResponseEntity.ok(updated);
    }
}
