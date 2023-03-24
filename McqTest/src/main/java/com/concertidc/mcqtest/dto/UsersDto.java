package com.concertidc.mcqtest.dto;

public class UsersDto {
	
	private Long userId;
	private String name;
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
	
	public UsersDto(){}
	public UsersDto(Long userId, String name) {
		this.userId = userId;
		this.name = name;
	}
	
}
