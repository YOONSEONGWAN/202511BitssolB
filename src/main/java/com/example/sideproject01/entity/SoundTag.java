package com.example.sideproject01.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class SoundTag {

	@Id // num 을 PK 로 설정
	//@GeneratedValue(strategy = GenerationType.AUTO) // 시퀀스를 자동으로 만들어줌
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sound_tag_seq_gen") // 충돌이 나서 잠시 해놨습니다. -재원
	@SequenceGenerator(name = "sound_tag_seq_gen", sequenceName = "sound_tag_seq", allocationSize = 1) // 충돌이 나서 잠시 해놨습니다. -재원
	@Column(name = "sound_tag_id") // 충돌이 나서 잠시 해놨습니다. -재원
	private Integer soundTagId;
	
	@ManyToOne
	@JoinColumn(name="sound_id", referencedColumnName="sound_id")
	private Sound soundId;
	
	@ManyToOne
	@JoinColumn(name="tag_id", referencedColumnName="tag_id")
	private Tag tagId;
}