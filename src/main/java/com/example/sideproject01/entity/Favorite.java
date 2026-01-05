package com.example.sideproject01.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Favorite {

	@Id // num 을 PK 로 설정
	//@GeneratedValue(strategy = GenerationType.AUTO) // 시퀀스를 자동으로 만들어줌
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favorite_seq_gen") // 충돌이 나서 잠시 해놨습니다. -재원
	@SequenceGenerator(name = "favorite_seq_gen", sequenceName = "favorite_seq", allocationSize = 1) // 충돌이 나서 잠시 해놨습니다. -재원
	@Column(name = "favorite_id") // 충돌이 나서 잠시 해놨습니다. -재원
	private Integer favoriteId;
	
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User userId;
	
	@ManyToOne
	@JoinColumn(name="sound_id",referencedColumnName="sound_id")
	private Sound soundId;
	
	private LocalDateTime createdAt;
}