package com.example.sideproject01.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sideproject01.dto.UserLoginRequestDto;
import com.example.sideproject01.dto.UserResponseDto;
import com.example.sideproject01.dto.UserSignupRequestDto;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.entity.UserRole;
import com.example.sideproject01.jwt.JwtTokenProvider;
import com.example.sideproject01.repository.UserRepository;
import com.example.sideproject01.util.PasswordPolicy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public Long signup(UserSignupRequestDto requestDto) {

        if (!hasText(requestDto.getName()) || !hasText(requestDto.getPassword())) {
            throw new IllegalArgumentException("아이디/비밀번호는 필수입니다.");
        }

        if (userRepository.existsByUserName(requestDto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // ✅ 비밀번호 정책(8~16)
        if (!PasswordPolicy.isValid(requestDto.getPassword())) {
            throw new IllegalArgumentException(
                "비밀번호는 8~16자이며 영문/숫자/특수문자를 각각 1자 이상 포함해야 합니다."
            );
        }

        // ✅ 선택값 중복 체크(값이 있을 때만)
        if (hasText(requestDto.getNickname()) && userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (hasText(requestDto.getEmail()) && userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String normalizedPhone = normalizePhone(requestDto.getPhone());
        if (hasText(normalizedPhone) && userRepository.existsByPhone(normalizedPhone)) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        String encoded = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .userName(requestDto.getName())
                .password(encoded)
                .role(UserRole.USER) // ✅ 서버에서 USER 고정 권장
                .nickname(requestDto.getNickname())
                .email(requestDto.getEmail())
                .phone(normalizedPhone)
                .build();

        return userRepository.save(user).getId();
    }

    @Override
    @Transactional
    public String login(UserLoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(requestDto.getName(), requestDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);
        return jwtTokenProvider.createToken(authentication);
    }

    @Override
    public UserResponseDto getMyInfo(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public void changeNickname(String userName, String newNickname) {
        if (!hasText(newNickname)) throw new IllegalArgumentException("닉네임이 비어있습니다.");

        if (userRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        user.setNickname(newNickname);
        userRepository.save(user);
    }

    // helpers
    private static boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
    private static String normalizePhone(String phone) {
        if (!hasText(phone)) return null;
        String digits = phone.replaceAll("\\D", "");
        return digits.isEmpty() ? null : digits;
    }
    
    @Override
    @Transactional
    public void changeEmail(String userName, String newEmail) {
        User user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 빈값이면 null로 “지움” 처리 (원하면 여기서 금지로 바꿔도 됨)
        String email = hasText(newEmail) ? newEmail.trim().toLowerCase() : null;

        // 본인 값과 동일하면 통과
        if ((email == null && user.getEmail() == null) || (email != null && email.equals(user.getEmail()))) {
            return;
        }

        if (email != null && userRepository.existsByEmailAndIdNot(email, user.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePhone(String userName, String newPhone) {
        User user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String digits = normalizePhone(newPhone); // null 또는 숫자만

        // 본인 값과 동일하면 통과
        if ((digits == null && user.getPhone() == null) || (digits != null && digits.equals(user.getPhone()))) {
            return;
        }

        if (digits != null && userRepository.existsByPhoneAndIdNot(digits, user.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        user.setPhone(digits);
        userRepository.save(user);
    }

}
