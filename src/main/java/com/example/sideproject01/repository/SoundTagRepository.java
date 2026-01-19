package com.example.sideproject01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sideproject01.entity.SoundTag;
import java.util.List;

public interface SoundTagRepository extends JpaRepository<SoundTag, Integer> {
    // 특정 Sound의 태그들 조회
    List<SoundTag> findBySoundId_SoundId(Integer soundId);
    
    
    // =========================
    // ✅ (추가 - 재원) 업로드 삭제 시 FK 제약 때문에 먼저 지워야 함
    // - SoundTag 테이블은 sound_id를 FK로 잡고 있어서,
    //   sound를 먼저 delete 하면 ORA-02292(자식 레코드 존재)로 실패할 수 있음
    // - 그래서 sound 삭제 전에 sound_tag 매핑을 먼저 삭제하는 용도
    // =========================
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query(
        "delete from SoundTag st where st.soundId.soundId = :soundId"
    )
    void deleteAllBySoundId(@org.springframework.data.repository.query.Param("soundId") Integer soundId);
}