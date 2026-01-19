package com.example.sideproject01.service;

import com.example.sideproject01.dto.ProfileUpdateRequestDto;
import com.example.sideproject01.dto.UserResponseDto;

public interface UserProfileService {
    UserResponseDto updateMyProfile(String userName, ProfileUpdateRequestDto dto);
}
