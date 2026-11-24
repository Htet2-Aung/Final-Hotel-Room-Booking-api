package com.hostmdy.hotelbooking.domain;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;



@Entity
@Data
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String invoiceNo; 
	
	private  String holderName;
	
	private Double total;
	
	private Long cardNo;
	
	private String cardType;

	/** Last 6 digits of the wallet transaction ID (manual KPay/Wave Pay flow). */
	private String transactionId;

	/** Base64 data URL of the uploaded transfer slip (manual KPay/Wave Pay flow). */
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String slipImage;
	
	private LocalDate createdAt;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JsonIgnore
	private Booking booking;
	
	@PrePersist
	void onCreatedAt() {
		this.createdAt=LocalDate.now();
	}
	


	
	
	
}