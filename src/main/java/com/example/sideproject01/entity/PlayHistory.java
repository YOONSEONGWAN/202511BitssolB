package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "play_history",
    // ✅ 같은 유저가 같은 사운드를 여러 번 재생해도 "행은 1개"만 유지(중복 제거)
    uniqueConstraints = @UniqueConstraint(
        name = "uk_play_user_sound",
        columnNames = {"user_id", "sound_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "play_history_seq_gen")
    @SequenceGenerator(
        name = "play_history_seq_gen",
        sequenceName = "play_history_seq",
        allocationSize = 1
    )
    @Column(name = "history_id")
    private Integer historyId;

    // ✅ 필드명이 userId지만 타입은 User(기존 스타일 유지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sound_id", referencedColumnName = "sound_id", nullable = false)
    private Sound soundId;

    @Column(nullable = false)
    private LocalDateTime playedAt;
}