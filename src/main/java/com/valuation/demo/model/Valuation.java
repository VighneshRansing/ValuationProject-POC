package com.valuation.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "valuations")
public class Valuation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String ownerName;

	private String ownerMobile;

	private Double carpetArea;

	private String possession; // e.g. "Ready", "Under Construction"

	private String address;

	@Column(name = "possession", length = 64)
	private String possessionColumn; // mapped to DB column 'possession' (backing field)

	private LocalDateTime createdAt;

	public Valuation() {
		this.createdAt = LocalDateTime.now();
	}

	// getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerMobile() {
		return ownerMobile;
	}

	public void setOwnerMobile(String ownerMobile) {
		this.ownerMobile = ownerMobile;
	}

	public Double getCarpetArea() {
		return carpetArea;
	}

	public void setCarpetArea(Double carpetArea) {
		this.carpetArea = carpetArea;
	}

	public String getPossession() {
		// prefer the logical possession field; fall back to DB-backed possessionColumn if used
		return possession != null && !possession.isEmpty() ? possession : possessionColumn;
	}

	public void setPossession(String possession) {
		this.possession = possession;
		this.possessionColumn = possession;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}



	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}