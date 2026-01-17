package com.example.sideproject01.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sideproject01.entity.Sound;
import com.example.sideproject01.entity.User;
import com.example.sideproject01.repository.FavoriteRepository;
import com.example.sideproject01.repository.PlayHistoryRepository;
import com.example.sideproject01.repository.SoundRepository;
import com.example.sideproject01.repository.SoundTagRepository;
import com.example.sideproject01.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageDeleteServiceImpl implements MyPageDeleteService {

    private final UserRepository userRepo;
    private final SoundRepository soundRepo;
    private final PlayHistoryRepository playHistoryRepo;
    private final FavoriteRepository favoriteRepo;
    private final SoundTagRepository soundTagRepo;

    @Value("${file.location}")
    private String fileLocation;

    /**
     * ✅ (공통) 현재 로그인 유저 조회
     * - SecurityContextHolder에서 username 뽑아서 User 엔티티로 변환
     * - 유저가 없으면 UsernameNotFoundException
     */
    private User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    /**
     * ✅ 최근 재생 삭제: "내 play_history 1건"만 삭제
     * - Sound 자체는 삭제하지 않음(콘텐츠 삭제가 아니라 내 기록 삭제)
     */
    @Override
    @Transactional
    public void deleteMyRecentPlay(Integer soundId) {
        User me = getCurrentUser();

        // ✅ user_id + sound_id 조건으로 내 기록만 삭제
        // (Repository에 추가한 deleteByUserAndSoundId 필요)
        playHistoryRepo.deleteByUserAndSoundId(me, soundId);
    }

    /**
     * ✅ 내 업로드 삭제: "진짜 삭제"
     * - 권한 체크: 업로더 본인만 삭제 가능
     * - FK 제약 때문에 자식 테이블 먼저 삭제 후 sound 삭제
     * - 마지막에 파일 삭제(파일 삭제 실패가 DB 롤백 유발하지 않게 처리)
     */
    @Override
    @Transactional
    public void deleteMyUpload(Integer soundId) {
        User me = getCurrentUser();

        // 1) Sound 조회
        Sound sound = soundRepo.findById(soundId)
                .orElseThrow(() -> new IllegalArgumentException("해당 소리를 찾을 수 없습니다."));

        // 2) 권한 체크: 업로더 본인인지 확인
        // - Sound.uploader는 ManyToOne이라 Lazy일 수 있으나,
        //   @Transactional 안에서는 보통 안전하게 접근 가능
        if (sound.getUploader() == null || sound.getUploader().getId() == null
                || !sound.getUploader().getId().equals(me.getId())) {
            throw new AccessDeniedException("내가 업로드한 콘텐츠만 삭제할 수 있습니다.");
        }

        // 3) 파일명 확보 (DB 삭제 전에 뽑아둬야 함)
        // - Sound.fileUrl: 현재 저장 방식상 UUID+원본명(파일명)이 들어감
        String soundFileName = sound.getFileUrl();

        // - Sound.thumbnailUrl: "/upload/xxx" 형태이므로 마지막 "/" 뒤가 파일명
        String thumbFileName = extractFileName(sound.getThumbnailUrl());

        // 4) FK 자식 테이블 먼저 삭제 (Oracle에서 부모 먼저 지우면 ORA-02292 가능)
        // 4-1) sound_tag 매핑 삭제
        soundTagRepo.deleteAllBySoundId(soundId);

        // 4-2) favorites 삭제 (다른 유저가 즐겨찾기한 것도 포함)
        favoriteRepo.deleteAllBySoundId(soundId);

        // 4-3) play_history 삭제 (전체 유저 기록 삭제)
        playHistoryRepo.deleteBySoundId(soundId);

        // 5) 이제 부모(sound) 삭제 가능
        soundRepo.delete(sound);

        // 6) 물리 파일 삭제
        // - 파일 삭제 실패로 DB 롤백이 되면 사용자 입장에선 더 큰 장애(삭제가 안 됨)
        // - 그래서 파일 삭제는 예외를 삼키거나 로그만 남기는 편이 안정적
        deleteFileIfExists(soundFileName);
        deleteFileIfExists(thumbFileName);
    }

    /**
     * ✅ "/upload/abc.png" 또는 "abc.png" 둘 다 대응
     */
    private String extractFileName(String urlOrName) {
        if (urlOrName == null || urlOrName.isBlank()) return null;
        int idx = urlOrName.lastIndexOf('/');
        return (idx >= 0) ? urlOrName.substring(idx + 1) : urlOrName;
    }

    /**
     * ✅ file.location 기준으로 파일 삭제
     */
    private void deleteFileIfExists(String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Path p = Paths.get(fileLocation + File.separator + fileName);
            Files.deleteIfExists(p);
        } catch (Exception ignore) {
            // (권장) logger.warn("파일 삭제 실패: {}", fileName, ignore);
        }
    }
}
