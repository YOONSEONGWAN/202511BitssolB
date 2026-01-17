package com.example.sideproject01.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.sideproject01.dto.ProfileImageResponseDto;
import com.example.sideproject01.service.UserProfileImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/me")
public class UserProfileImageController {

    private final UserProfileImageService userProfileImageService;

    @GetMapping("/profile-image")
    public ResponseEntity<ProfileImageResponseDto> getMyProfileImage(Principal principal) {
        return ResponseEntity.ok(userProfileImageService.getMyProfileImage(principal.getName()));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<ProfileImageResponseDto> updateMyProfileImage(
            Principal principal,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userProfileImageService.updateMyProfileImage(principal.getName(), file));
    }

    @DeleteMapping("/profile-image")
    public ResponseEntity<ProfileImageResponseDto> resetMyProfileImage(Principal principal) {
        return ResponseEntity.ok(userProfileImageService.resetMyProfileImage(principal.getName()));
    }
}

