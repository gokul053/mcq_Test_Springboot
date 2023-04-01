package com.concertidc.mcqtest.model;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
@Entity
public class Options {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Max(10)
	private Long questionNumber;
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
	public Options() {
		
	}
	public Options(Long questionNumber, String optionA, String optionB, String optionC, String optionD) {
		this.questionNumber = questionNumber;
		this.optionA = optionA;
		this.optionB = optionB;
		this.optionC = optionC;
		this.optionD = optionD;
	}
	
}
