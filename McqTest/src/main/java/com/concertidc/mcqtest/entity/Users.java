package com.concertidc.mcqtest.entity;




import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long userId;
	private String name;
	private String department;
	private String address;
	
	@OneToOne(mappedBy = "users")
    private AnswerSheet answerSheet;
	
	public AnswerSheet getAnswerSheet() {
		return answerSheet;
	}
	public void setAnswerSheet(AnswerSheet answerSheet) {
		this.answerSheet = answerSheet;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Users(){
	}
	public Users(Long userId, String name, String department, String address) {
		this.userId = userId;
		this.name = name;
		this.department = department;
		this.address = address;
	}
	
}
