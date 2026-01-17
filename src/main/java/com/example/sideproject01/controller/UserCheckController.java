package com.example.sideproject01.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.sideproject01.dto.AvailabilityResponseDto;
import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users/check")
public class UserCheckController {

    private final UserRepository userRepository;

    @GetMapping("/username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam("name") String name) {
        boolean available = !userRepository.existsByUserName(name.trim());
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam("nickname") String nickname) {
        boolean available = !userRepository.existsByNickname(nickname.trim());
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        String normalized = email.trim().toLowerCase();
        boolean available = !userRepository.existsByEmail(normalized);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/phone")
    public ResponseEntity<Map<String, Boolean>> checkPhone(@RequestParam("phone") String phone) {
        String digits = phone.replaceAll("\\D", "");
        boolean available = digits.isEmpty() || !userRepository.existsByPhone(digits);
        return ResponseEntity.ok(Map.of("available", available));
    }
}

