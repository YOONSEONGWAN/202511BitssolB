package com.example.sideproject01.service;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sideproject01.entity.PlayHistory;
import com.example.sideproject01.entity.Sound;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.PlayHistoryRepository;
import com.example.sideproject01.repository.SoundRepository;
import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecentPlayService {

    private final UserRepository userRepo;
    private final SoundRepository soundRepo;
    private final PlayHistoryRepository playHistoryRepo;

    private User getCurrentUser() {
        // ✅ FavoriteServiceImpl과 동일한 방식(토큰에서 userName 추출)
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * ✅ 최근 재생 저장(중복 제거)
     * - 같은 유저가 같은 곡을 다시 재생해도 행은 1개만 유지
     * - playedAt만 최신으로 갱신
     * - 조회수(playCount)는 여기서 올리지 않음(분리)
     */
    @Transactional
    public void recordRecentPlay(Integer soundId) {
        User user = getCurrentUser();

        Sound sound = soundRepo.findById(soundId)
                .orElseThrow(() -> new IllegalArgumentException("해당 소리를 찾을 수 없습니다."));

        playHistoryRepo.findByUserIdAndSoundId(user, sound)
                .ifPresentOrElse(ph -> {
                    // ✅ 중복이면 시간만 갱신
                    ph.setPlayedAt(LocalDateTime.now());
                }, () -> {
                    // ✅ 처음 재생이면 신규 생성
                    PlayHistory ph = PlayHistory.builder()
                            .userId(user)
                            .soundId(sound)
                            .playedAt(LocalDateTime.now())
                            .build();
                    playHistoryRepo.save(ph);
                });
    }
}
