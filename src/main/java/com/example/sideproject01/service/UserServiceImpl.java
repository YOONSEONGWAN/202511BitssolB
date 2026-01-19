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

        String userName = requestDto.getName().trim();

        // ✅ existsByUserName -> countByUserName
        if (userRepository.countByUserName(userName) > 0) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        if (!PasswordPolicy.isValid(requestDto.getPassword())) {
            throw new IllegalArgumentException(
                    "비밀번호는 8~16자이며 영문/숫자/특수문자를 각각 1자 이상 포함해야 합니다."
            );
        }

        // ✅ 선택값 중복 체크(count 기반)
        if (hasText(requestDto.getNickname())) {
            String nick = requestDto.getNickname().trim();
            if (userRepository.countByNickname(nick) > 0) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }

        if (hasText(requestDto.getEmail())) {
            String email = requestDto.getEmail().trim().toLowerCase();
            if (userRepository.countByEmail(email) > 0) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }

        String normalizedPhone = normalizePhone(requestDto.getPhone());
        if (hasText(normalizedPhone)) {
            if (userRepository.countByPhone(normalizedPhone) > 0) {
                throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
            }
        }

        String encoded = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .userName(userName)
                .password(encoded)
                .role(UserRole.USER)
                .nickname(hasText(requestDto.getNickname()) ? requestDto.getNickname().trim() : null)
                .email(hasText(requestDto.getEmail()) ? requestDto.getEmail().trim().toLowerCase() : null)
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

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String nick = newNickname.trim();

        // ✅ 본인 제외 중복 체크(countBy...AndIdNot)
        if (userRepository.countByNicknameAndIdNot(nick, user.getId()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        user.setNickname(nick);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeEmail(String userName, String newEmail) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String email = hasText(newEmail) ? newEmail.trim().toLowerCase() : null;

        if ((email == null && user.getEmail() == null) || (email != null && email.equals(user.getEmail()))) {
            return;
        }

        // ✅ existsByEmailAndIdNot -> countByEmailAndIdNot
        if (email != null && userRepository.countByEmailAndIdNot(email, user.getId()) > 0) {
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

        if ((digits == null && user.getPhone() == null) || (digits != null && digits.equals(user.getPhone()))) {
            return;
        }

        // ✅ existsByPhoneAndIdNot -> countByPhoneAndIdNot
        if (digits != null && userRepository.countByPhoneAndIdNot(digits, user.getId()) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        user.setPhone(digits);
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
}
