package com.example.sideproject01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.sideproject01.entity.Favorite;
import com.example.sideproject01.entity.Sound;
import com.example.sideproject01.entity.User;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(User userId);
    Optional<Favorite> findByUserIdAndSoundId(User userId, Sound soundId);
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favorite f WHERE f.userId = :userId AND f.soundId = :soundId")
    boolean existsByUserIdAndSoundId(@Param("userId") User userId, @Param("soundId") Sound soundId);
    void deleteByUserIdAndSoundId(User userId, Sound soundId);
    
    
    // =========================
    // ✅ (추가 - 재원) 업로드 삭제 시 "다른 유저가 즐겨찾기한 것"도 같이 제거
    // - Favorite 테이블도 sound_id FK가 있어서,
    //   sound 삭제 전에 즐겨찾기 레코드를 먼저 삭제해야 함
    // - "진짜 삭제" 정책이면, 소유자(업로더)가 지울 때 관련 데이터까지 정리하는 게 일반적
    // =========================
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Favorite f where f.soundId.soundId = :soundId")
    void deleteAllBySoundId(@Param("soundId") Integer soundId);
}
