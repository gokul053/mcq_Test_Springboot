package com.concertidc.mcqtest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class UserAnswers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerId;
	@NotNull
	private String answer;

	@OneToOne
	@JoinColumn(name = "question_no")
	private Questions questions;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users users;

	public Questions getQuestions() {
		return questions;
	}

	public void setQuestions(Questions questions) {
		this.questions = questions;
	}

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

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public UserAnswers() {
		
	}
	public UserAnswers(Long answerId, @NotNull String answer, Questions questions, Users users) {
		this.answerId = answerId;
		this.answer = answer;
		this.questions = questions;
		this.users = users;
	}

	
	
}