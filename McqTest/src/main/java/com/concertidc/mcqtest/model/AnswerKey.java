package com.concertidc.mcqtest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class AnswerKey {

	@Id
	private Long keyId;

	@NotNull
	private String answerKey;

	public Long getKeyId() {
		return keyId;
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public String getAnswerKey() {
		return answerKey;
	}

	public void setAnswerKey(String answerKey) {
		this.answerKey = answerKey;
	}

	public AnswerKey(Long keyId, String answerKey) {
		this.keyId = keyId;
		this.answerKey = answerKey;
	}

	public AnswerKey() {
	}
}