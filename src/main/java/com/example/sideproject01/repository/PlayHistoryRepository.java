package com.example.sideproject01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sideproject01.entity.PlayHistory;
import com.example.sideproject01.entity.Sound;
import com.example.sideproject01.entity.User;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Integer> {

    // ✅ upsert(있으면 갱신 / 없으면 생성)용 - 그대로 유지
    Optional<PlayHistory> findByUserIdAndSoundId(User userId, Sound soundId);

    // ✅  Oracle 11g 대응: Pageable 제거 → fetch first 문법 생성 자체를 차단
    // ✅ EntityGraph로 sound + uploader까지 같이 로딩 (N+1 / Lazy 예외 방지)
    @EntityGraph(attributePaths = {"soundId", "soundId.uploader"})
    List<PlayHistory> findByUserIdOrderByPlayedAtDesc(User userId);
    
    // =========================
    // ✅ (추가 - 재원) 최근 재생 "내 기록만" 삭제
    // - 최근 재생은 Sound 자체가 아니라 PlayHistory(내 활동 로그)만 지우는 개념
    // - 따라서 user_id + sound_id 조건으로 1건 삭제
    // =========================
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query(
        "delete from PlayHistory ph where ph.userId = :user and ph.soundId.soundId = :soundId"
    )
    void deleteByUserAndSoundId(
        @org.springframework.data.repository.query.Param("user") User user,
        @org.springframework.data.repository.query.Param("soundId") Integer soundId
    );

    // =========================
    // ✅ (추가 - 재원) 업로드 삭제 시 "전체 유저의 최근 재생 기록" 삭제
    // - 업로드한 Sound를 진짜 삭제하면,
    //   다른 유저들의 play_history(자식 레코드)도 FK 때문에 먼저 지워야 함
    // =========================
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query(
        "delete from PlayHistory ph where ph.soundId.soundId = :soundId"
    )
    void deleteBySoundId(@org.springframework.data.repository.query.Param("soundId") Integer soundId);
}