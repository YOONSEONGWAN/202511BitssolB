package com.example.sideproject01.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/check")
public class UserCheckController {

    private final UserRepository userRepository;

    @GetMapping("/username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("name") String name) {
        String normalized = normalizeText(name);
        // 빈 값이면 사용 가능(false/true 정책은 팀 규칙에 맞춰 결정)
        if (normalized.isEmpty()) {
            return ResponseEntity.ok(Map.of("available", false));
        }

        boolean available = userRepository.countByUserName(normalized) == 0;
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam("nickname") String nickname) {
        String normalized = normalizeText(nickname);
        if (normalized.isEmpty()) {
            return ResponseEntity.ok(Map.of("available", false));
        }

        boolean available = userRepository.countByNickname(normalized) == 0;
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        String normalized = normalizeEmail(email);
        // 이메일은 빈 값이면 “선택값”으로 허용할지 정책에 따라 달라짐
        if (normalized.isEmpty()) {
            return ResponseEntity.ok(Map.of("available", true));
        }

        boolean available = userRepository.countByEmail(normalized) == 0;
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/phone")
    public ResponseEntity<Map<String, Boolean>> checkPhone(@RequestParam("phone") String phone) {
        String digits = normalizePhone(phone);
        // 전화번호도 선택값이면 빈 값은 허용(true)
        if (digits.isEmpty()) {
            return ResponseEntity.ok(Map.of("available", true));
        }

        boolean available = userRepository.countByPhone(digits) == 0;
        return ResponseEntity.ok(Map.of("available", available));
    }

    // --------------------
    // normalize helpers
    // --------------------

    private static String normalizeText(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String normalizeEmail(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }

    private static String normalizePhone(String s) {
        if (s == null) return "";
        return s.replaceAll("\\D", ""); // 숫자만
    }
}
