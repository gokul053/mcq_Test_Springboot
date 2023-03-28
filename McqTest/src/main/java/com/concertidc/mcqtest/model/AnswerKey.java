package com.concertidc.mcqtest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AnswerKey {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long keyId;
	private String answerKeyOne;
	private String answerKeyTwo;
	private String answerKeyThree;
	private String answerKeyFour;
	private String answerKeyFive;
	private String answerKeySix;
	private String answerKeySeven;
	private String answerKeyEight;
	private String answerKeyNine;
	private String answerKeyTen;
	public Long getKeyId() {
		return keyId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public String getAnswerKeyOne() {
		return answerKeyOne;
	}
	public void setAnswerKeyOne(String answerKeyOne) {
		this.answerKeyOne = answerKeyOne;
	}
	public String getAnswerKeyTwo() {
		return answerKeyTwo;
	}
	public void setAnswerKeyTwo(String answerKeyTwo) {
		this.answerKeyTwo = answerKeyTwo;
	}
	public String getAnswerKeyThree() {
		return answerKeyThree;
	}
	public void setAnswerKeyThree(String answerKeyThree) {
		this.answerKeyThree = answerKeyThree;
	}
	public String getAnswerKeyFour() {
		return answerKeyFour;
	}
	public void setAnswerKeyFour(String answerKeyFour) {
		this.answerKeyFour = answerKeyFour;
	}
	public String getAnswerKeyFive() {
		return answerKeyFive;
	}
	public void setAnswerKeyFive(String answerKeyFive) {
		this.answerKeyFive = answerKeyFive;
	}
	public String getAnswerKeySix() {
		return answerKeySix;
	}
	public void setAnswerKeySix(String answerKeySix) {
		this.answerKeySix = answerKeySix;
	}
	public String getAnswerKeySeven() {
		return answerKeySeven;
	}
	public void setAnswerKeySeven(String answerKeySeven) {
		this.answerKeySeven = answerKeySeven;
	}
	public String getAnswerKeyEight() {
		return answerKeyEight;
	}
	public void setAnswerKeyEight(String answerKeyEight) {
		this.answerKeyEight = answerKeyEight;
	}
	public String getAnswerKeyNine() {
		return answerKeyNine;
	}
	public void setAnswerKeyNine(String answerKeyNine) {
		this.answerKeyNine = answerKeyNine;
	}
	public String getAnswerKeyTen() {
		return answerKeyTen;
	}
	public void setAnswerKeyTen(String answerKeyTen) {
		this.answerKeyTen = answerKeyTen;
	}
	public AnswerKey() {
		
	}
	public AnswerKey(Long keyId, String answerKeyOne, String answerKeyTwo, String answerKeyThree, String answerKeyFour,
			String answerKeyFive, String answerKeySix, String answerKeySeven, String answerKeyEight,
			String answerKeyNine, String answerKeyTen) {
		this.keyId = keyId;
		this.answerKeyOne = answerKeyOne;
		this.answerKeyTwo = answerKeyTwo;
		this.answerKeyThree = answerKeyThree;
		this.answerKeyFour = answerKeyFour;
		this.answerKeyFive = answerKeyFive;
		this.answerKeySix = answerKeySix;
		this.answerKeySeven = answerKeySeven;
		this.answerKeyEight = answerKeyEight;
		this.answerKeyNine = answerKeyNine;
		this.answerKeyTen = answerKeyTen;
	}
	
}
