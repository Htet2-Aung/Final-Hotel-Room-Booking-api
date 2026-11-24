package com.hostmdy.hotelbooking.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class ActivityLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String action;
	
	@Column(length = 1000)
	private String details;
	
	private String username;
	
	private LocalDateTime createdAt;
	
	@PrePersist
	void onCreatedAt() {
		this.createdAt = LocalDateTime.now();
	}
}
