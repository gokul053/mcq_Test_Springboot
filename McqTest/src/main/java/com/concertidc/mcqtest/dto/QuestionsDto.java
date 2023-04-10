package com.concertidc.mcqtest.dto;

public class QuestionsDto {

	private Long questionNumber;
	private String question;
	private String a;
	private String b;
	private String c;
	private String d;

	public Long getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(Long questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public QuestionsDto(Long questionNumber, String question, String a, String b, String c, String d) {
		this.questionNumber = questionNumber;
		this.question = question;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	public QuestionsDto() {
	}

}
