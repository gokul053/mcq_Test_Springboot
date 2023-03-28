package com.concertidc.mcqtest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class AnswerSheet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerId;
	private String answerOne;
	private String answerTwo;
	private String answerThree;
	private String answerFour;
	private String answerFive;
	private String answerSix;
	private String answerSeven;
	private String answerEight;
	private String answerNine;
	private String answerTen;
	
	@OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId", unique = true)
	@JsonIgnore
	private Users users;
	
	
	
	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public Long getAnswerId() {
		return answerId;
	}
	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}
	public String getAnswerOne() {
		return answerOne;
	}
	public void setAnswerOne(String answerOne) {
		this.answerOne = answerOne;
	}
	public String getAnswerTwo() {
		return answerTwo;
	}
	public void setAnswerTwo(String answerTwo) {
		this.answerTwo = answerTwo;
	}
	public String getAnswerThree() {
		return answerThree;
	}
	public void setAnswerThree(String answerThree) {
		this.answerThree = answerThree;
	}
	public String getAnswerFour() {
		return answerFour;
	}
	public void setAnswerFour(String answerFour) {
		this.answerFour = answerFour;
	}
	public String getAnswerFive() {
		return answerFive;
	}
	public void setAnswerFive(String answerFive) {
		this.answerFive = answerFive;
	}
	public String getAnswerSix() {
		return answerSix;
	}
	public void setAnswerSix(String answerSix) {
		this.answerSix = answerSix;
	}
	public String getAnswerSeven() {
		return answerSeven;
	}
	public void setAnswerSeven(String answerSeven) {
		this.answerSeven = answerSeven;
	}
	public String getAnswerEight() {
		return answerEight;
	}
	public void setAnswerEight(String answerEight) {
		this.answerEight = answerEight;
	}
	public String getAnswerNine() {
		return answerNine;
	}
	public void setAnswerNine(String answerNine) {
		this.answerNine = answerNine;
	}
	public String getAnswerTen() {
		return answerTen;
	}
	public void setAnswerTen(String answerTen) {
		this.answerTen = answerTen;
	}
	public AnswerSheet() {}
	public AnswerSheet(Long answerId, String answerOne, String answerTwo, String answerThree, String answerFour,
			String answerFive, String answerSix, String answerSeven, String answerEight, String answerNine,
			String answerTen) {
		this.answerOne = answerOne;
		this.answerTwo = answerTwo;
		this.answerThree = answerThree;
		this.answerFour = answerFour;
		this.answerFive = answerFive;
		this.answerSix = answerSix;
		this.answerSeven = answerSeven;
		this.answerEight = answerEight;
		this.answerNine = answerNine;
		this.answerTen = answerTen;
		this.answerId = answerId;
	}
	
}
