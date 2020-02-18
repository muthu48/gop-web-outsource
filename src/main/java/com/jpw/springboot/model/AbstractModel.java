package com.jpw.springboot.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class AbstractModel {
	@CreatedDate
	@JsonDeserialize(using = MyCustomDeserializer.class)
	@JsonSerialize(using = MyCustomSerializer.class)
	private Instant createdDate;

	@LastModifiedDate
	@JsonDeserialize(using = MyCustomDeserializer.class)
	@JsonSerialize(using = MyCustomSerializer.class)
	private Instant lastModifiedDate;

	public Instant getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Instant createdDate) {
		this.createdDate = createdDate;
	}

	public Instant getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Instant lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}
