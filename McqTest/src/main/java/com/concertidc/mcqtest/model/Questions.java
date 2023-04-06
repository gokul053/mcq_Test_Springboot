package com.concertidc.mcqtest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

@Entity
public class Questions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Max(10)
	private Long questionId;
	@NotNull
	private String question;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "questionId")
	private Options options;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "answerKeyId")
	private AnswerKey answerKey;

	public AnswerKey getAnswerKey() {
		return answerKey;
	}

	public void setAnswerKey(AnswerKey answerKey) {
		this.answerKey = answerKey;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Questions() {

	}

	public Questions(Long questionId, String question) {
		this.questionId = questionId;
		this.question = question;
	}
}
