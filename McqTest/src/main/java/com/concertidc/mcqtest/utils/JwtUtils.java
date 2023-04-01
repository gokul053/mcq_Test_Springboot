package com.concertidc.mcqtest.utils;

import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

	@Value("${app.secret.key}")
	private String secret_key;

	// code to generate Token
	public String generateToken(String subject) {
		String tokenId= String.valueOf(new Random().nextInt(10000));
		return Jwts.builder()
				.setId(tokenId)
				.setSubject(subject)
				.setIssuer("gokul_d")
				.setAudience("concert_idc")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5)))
				.signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(secret_key.getBytes()))
				.compact();
	}

	// code to get Claims
	public Claims getClaims(String token) {

		return Jwts.parser()
				.setSigningKey(Base64.getEncoder().encode(secret_key.getBytes()))
				.parseClaimsJws(token)
				.getBody();
	}

	// code to check if token is valid
	public boolean isValidToken(String token) {
		return getClaims(token).getExpiration().after(new Date(System.currentTimeMillis()));
	}
	
	// code to check if token is valid as per username
	public boolean isValidToken(String token,String username) {
		String tokenUserName=getSubject(token);
		return (username.equals(tokenUserName) && !isTokenExpired(token));
	}
	
	// code to check if token is expired
	public boolean isTokenExpired(String token) {
		return getExpirationDate(token).before(new Date(System.currentTimeMillis()));
	}
	
	//code to get expiration date
	public Date getExpirationDate(String token) {
		return getClaims(token).getExpiration();
	}
	//code to get subject
	public String getSubject(String token) {
		return getClaims(token).getSubject();
	}
}
