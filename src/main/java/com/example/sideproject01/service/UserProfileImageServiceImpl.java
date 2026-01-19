package com.example.sideproject01.service;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.sideproject01.dto.ProfileImageResponseDto;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.UserRepository;
import com.example.sideproject01.service.UserProfileImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileImageServiceImpl implements UserProfileImageService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileImageResponseDto getMyProfileImage(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userName=" + userName));

        return ProfileImageResponseDto.builder()
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    @Override
    @Transactional
    public ProfileImageResponseDto updateMyProfileImage(String userName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드 파일이 비어있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        long max = 2 * 1024 * 1024;
        if (file.getSize() > max) {
            throw new IllegalArgumentException("이미지 용량은 2MB 이하만 가능합니다.");
        }

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userName=" + userName));

        try {
            String ext = getExt(file.getOriginalFilename());
            String savedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

            // 실제 디스크 저장 위치(폴더명은 upload여도 OK)
            Path uploadDir = Paths.get("upload", "profile");
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(savedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // ✅ DB에는 /upload 로 통일해서 저장
            user.setProfileImageUrl("/upload/profile/" + savedName);

            return ProfileImageResponseDto.builder()
                    .profileImageUrl(user.getProfileImageUrl())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    private String getExt(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0) return "";
        return filename.substring(idx + 1).toLowerCase();
    }
    
    @Override
    @Transactional
    public ProfileImageResponseDto resetMyProfileImage(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userName=" + userName));

        // ✅ 기본으로: DB 값을 비워둔다(null)
        user.setProfileImageUrl(null);

        return ProfileImageResponseDto.builder()
                .profileImageUrl(null)
                .build();
    }
}
