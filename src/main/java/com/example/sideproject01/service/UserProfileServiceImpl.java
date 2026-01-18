package com.example.sideproject01.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sideproject01.dto.ProfileUpdateRequestDto;
import com.example.sideproject01.dto.UserResponseDto;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDto updateMyProfile(String userName, ProfileUpdateRequestDto dto) {

        if (dto == null) throw new IllegalArgumentException("요청 값이 비어있습니다.");

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // nickname
        if (hasText(dto.getNickname())) {
            String newNick = dto.getNickname().trim();
            if (!newNick.equals(user.getNickname())
                    && userRepository.countByNickname(newNick) > 0) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
            user.setNickname(newNick);
        }

        // email
        if (hasText(dto.getEmail())) {
            String newEmail = dto.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail())
                    && userRepository.countByEmail(newEmail) > 0) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            user.setEmail(newEmail);
        }

        // phone
        if (hasText(dto.getPhone())) {
            String newPhone = normalizePhone(dto.getPhone());
            if (!newPhone.equals(user.getPhone())
                    && userRepository.countByPhone(newPhone) > 0) {
                throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
            }
            user.setPhone(newPhone);
        }

        return UserResponseDto.from(user);
    }

    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String normalizePhone(String phone) {
        String digits = phone.replaceAll("\\D", "");
        if (digits.isEmpty()) throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다.");
        return digits;
    }
}
