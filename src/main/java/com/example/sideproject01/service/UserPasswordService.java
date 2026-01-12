package com.example.sideproject01.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.sideproject01.dto.PasswordChangeRequestDto;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ 로그인한 사용자 본인 비밀번호 변경
     * @param userName principal.getName() (JWT subject)
     * @param dto currentPassword / newPassword
     */
    @Transactional
    public void changeMyPassword(String userName, PasswordChangeRequestDto dto) {

        // 1) 기본 유효성 검사(값이 비면 400)
        if (dto.getCurrentPassword() == null || dto.getNewPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호 값이 비어있습니다.");
        }

        // 2) 로그인한 사용자 조회 (userName = principal.getName())
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));

        // 3) 현재 비밀번호 검증 (평문 currentPassword vs DB 해시 password)
        boolean matches = passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword());
        if (!matches) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다.");
        }

        // 4) 새 비밀번호가 기존과 같은지 방지(선택)
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "새 비밀번호가 기존 비밀번호와 같습니다.");
        }

        // 5) 새 비밀번호 저장(해싱해서 저장)
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        // 6) 저장
        // - JPA라면 트랜잭션 종료 시 자동 반영되기도 하지만, 명시적으로 save 해도 OK
        userRepository.save(user);
    }
}
