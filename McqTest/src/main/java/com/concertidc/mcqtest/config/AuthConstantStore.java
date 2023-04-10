package com.concertidc.mcqtest.config;

public class AuthConstantStore {

	public static final int TOKEN_ID = 1000;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String TOKEN_ISSUER = "mcqtest.com";
	public static final String TOKEN_AUDIENCE = "students";
	public static final String TOKEN_TYPE_A = "AccessToken";
	public static final String TOKEN_TYPE_B = "RefreshToken";
	public static final String HEADER_STRING = "Authorization";
	public static final String HEADER_STRING_REFRESH = "isRefreshToken";
	public static final String COMMON_URL = "/home/**";
	public static final String ADMIN_URL = "/admin/**";
	public static final String USER_URL = "/user/**";
	public static final String ROLE_ADMIN = "Admin";
	public static final String ROLE_USER = "User";

}
