	package com.concertidc.mcqtest.dto;


public class UserResponse {

	private Long userId;
	private String username;
	private String email;
	private String name;
	private String department;
	private String address;
	private String token;
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public UserResponse() {}
	
	public UserResponse(Long userId, String username, String email, String name, String department, String address,
			String token) {
		this.userId = userId;
		this.username = username;
		this.email = email;
		this.name = name;
		this.department = department;
		this.address = address;
		this.token = token;
	}
	
}
