package com.concertidc.mcqtest.dto;

public class QuestionsDto {
	
	private Long questionNumber;
	private String question;
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
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
	public String getOptionA() {
		return optionA;
	}
	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}
	public String getOptionB() {
		return optionB;
	}
	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}
	public String getOptionC() {
		return optionC;
	}
	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}
	public String getOptionD() {
		return optionD;
	}
	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}
	public QuestionsDto() {}
	public QuestionsDto(Long questionNumber, String question, String optionA, String optionB, String optionC,
			String optionD) {
		this.questionNumber = questionNumber;
		this.question = question;
		this.optionA = optionA;
		this.optionB = optionB;
		this.optionC = optionC;
		this.optionD = optionD;
	}
	
	
}
