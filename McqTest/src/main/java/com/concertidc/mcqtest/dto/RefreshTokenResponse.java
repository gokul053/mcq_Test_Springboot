package com.concertidc.mcqtest.dto;

public class RefreshTokenResponse {

	private String username;
	private String newAccessToken;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNewAccessToken() {
		return newAccessToken;
	}

	public void setNewAccessToken(String newAccessToken) {
		this.newAccessToken = newAccessToken;
	}

	public RefreshTokenResponse() {
	}

	public RefreshTokenResponse(String username, String newAccessToken) {
		this.newAccessToken = newAccessToken;
	}

}
