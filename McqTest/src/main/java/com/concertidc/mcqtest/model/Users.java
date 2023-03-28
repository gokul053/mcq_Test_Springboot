package com.concertidc.mcqtest.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users",
uniqueConstraints = {
		@UniqueConstraint(columnNames = "userName"),
		@UniqueConstraint(columnNames = "email")
})
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long userId;
	private String userName;
	private String email;
	private String passWord;
	private String name;
	private String department;
	private String address;
	
	@OneToOne(mappedBy = "users", fetch = FetchType.EAGER)
    private AnswerSheet answerSheet;
	
	
	@ManyToMany(fetch = FetchType.LAZY)
	  @JoinTable(name = "user_roles", 
	             joinColumns = @JoinColumn(name = "user_id"),
	             inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
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
	public AnswerSheet getAnswerSheet() {
		return answerSheet;
	}
	public void setAnswerSheet(AnswerSheet answerSheet) {
		this.answerSheet = answerSheet;
	}
	public Users(){
	}
	public Users(String userName, String email, String passWord) {
		this.userName = userName;
		this.email = email;
		this.passWord = passWord;
	}
	
}
