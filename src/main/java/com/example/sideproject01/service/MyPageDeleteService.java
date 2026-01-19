package com.example.sideproject01.service;

/**
 * ✅ 마이페이지 삭제 전용 서비스 인터페이스
 * - 컨트롤러는 인터페이스 타입에 의존하게 해서(추상화)
 *   구현 교체/테스트(Mock) 용이하게 만드는 목적
 */
public interface MyPageDeleteService {

    /**
     * ✅ 최근 재생 삭제(내 기록만 삭제)
     * - PlayHistory에서 로그인 유저 + soundId 1건 삭제
     */
    void deleteMyRecentPlay(Integer soundId);

    /**
     * ✅ 내 업로드 삭제(진짜 삭제)
     * - 권한 체크(업로더 본인)
     * - FK 자식 데이터 삭제 후 Sound 삭제
     * - 물리 파일 삭제
     */
    void deleteMyUpload(Integer soundId);
}
