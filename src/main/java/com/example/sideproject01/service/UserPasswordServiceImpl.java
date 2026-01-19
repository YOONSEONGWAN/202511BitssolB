package com.example.sideproject01.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.sideproject01.dto.PasswordChangeRequestDto;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.UserRepository;
import com.example.sideproject01.util.PasswordPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPasswordServiceImpl implements UserPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changeMyPassword(String userName, PasswordChangeRequestDto dto) {

        // 1) 기본 유효성 검사(값이 비면 400)
        if (dto == null || dto.getCurrentPassword() == null || dto.getNewPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 값이 비어있습니다.");
        }

        // 1-1) ✅ 비밀번호 정책(8~16, 영문/숫자/특수문자)
        if (!PasswordPolicy.isValid(dto.getNewPassword())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "비밀번호는 8~16자이며 영문/숫자/특수문자를 각각 1자 이상 포함해야 합니다."
            );
        }

        // 2) 로그인한 사용자 조회
        User user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));

        // 3) 현재 비밀번호 검증
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        // 4) 새 비밀번호가 기존과 같은지 방지(선택)
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호가 기존 비밀번호와 같습니다.");
        }

        // 5) 새 비밀번호 저장(해싱)
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        // 6) 저장 (명시적으로)
        userRepository.save(user);
    }
}
