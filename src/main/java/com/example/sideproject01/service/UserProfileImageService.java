package com.example.sideproject01.service;

import org.springframework.web.multipart.MultipartFile;
import com.example.sideproject01.dto.ProfileImageResponseDto;

public interface UserProfileImageService {

    ProfileImageResponseDto getMyProfileImage(String userName);

    ProfileImageResponseDto updateMyProfileImage(String userName, MultipartFile file);
    
    ProfileImageResponseDto resetMyProfileImage(String userName);
}
