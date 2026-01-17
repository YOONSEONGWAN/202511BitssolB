package com.example.sideproject01.dto;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageSoundCardDto {
    private Integer soundId;
    private String title;
    private String thumbnailUrl;
    private Integer playCount;      // ✅ 표시가 필요하면 쓰고, 아니면 프론트에서 무시 가능
    private String uploader;
    private LocalDateTime playedAt; // ✅ recent-plays 전용
    private LocalDateTime createdAt;// ✅ uploads 전용
}
