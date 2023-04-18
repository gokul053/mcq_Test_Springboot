package com.concertidc.mcqtest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Options {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionNumber;
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

	public Options() {

	}

	public Options(Long questionNumber, String a, String b, String c, String d) {
		this.questionNumber = questionNumber;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	
}