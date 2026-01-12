package com.example.sideproject01.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Tag {
	
	@Id // tag_id 을 PK 로 설정
	//@GeneratedValue(strategy = GenerationType.AUTO) // 시퀀스를 자동으로 만들어줌
	//@SequenceGenerator(name = "tag_seq", sequenceName = "TAG_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tag_seq_gen") // 에러가 나서 잠깐 해놨습니다 - 재원
	@SequenceGenerator(name = "tag_seq_gen", sequenceName = "TAG_SEQ", allocationSize = 1) // 에러가 나서 잠깐 해놨습니다 - 재원
	@Column(name = "tag_id")
	private Integer tagId;
	
	private String name;
}