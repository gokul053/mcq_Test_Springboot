package com.concertidc.mcqtest.utils;

import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.concertidc.mcqtest.config.AuthConstantStore;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

	@Value("${app.secret.key}")
	private String secretKey;
	
	@Value("${app.accesstoken.expiryinminutes}")
	private int accessTokenExpirationInMinutes;
	
	@Value("${app.refreshtoken.expiryinhours}")
	private int refreshTokenExpirationInHours;
	

	// code to generate Token
	public String generateToken(String subject) {
		final String tokenId = String.valueOf(new Random().nextInt(AuthConstantStore.TOKEN_ID));
		return Jwts.builder()
				.setId(tokenId)
				.setHeaderParam("typ", AuthConstantStore.TOKEN_TYPE_A)
				.setSubject(subject)
				.setIssuer(AuthConstantStore.TOKEN_ISSUER)
				.setAudience(AuthConstantStore.TOKEN_AUDIENCE)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(this.accessTokenExpirationInMinutes)))
				.signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(this.secretKey.getBytes())).compact();
	}

	// code to generate Refresh Token
	public String generateRefreshToken(String subject) {
		final String tokenId = String.valueOf(new Random().nextInt(AuthConstantStore.TOKEN_ID));
		return Jwts.builder().setId(tokenId).setHeaderParam("typ", AuthConstantStore.TOKEN_TYPE_B)
				.setSubject(subject)
				.setIssuer(AuthConstantStore.TOKEN_ISSUER)
				.setAudience(AuthConstantStore.TOKEN_AUDIENCE)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(this.refreshTokenExpirationInHours)))
				.signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encode(this.secretKey.getBytes())).compact();
	}

	// code to get Claims
	public Claims getClaims(String rawtoken) {

		if (rawtoken.startsWith(AuthConstantStore.TOKEN_PREFIX)) {
			final String token = rawtoken.substring(7, rawtoken.length());
			return Jwts.parser().setSigningKey(Base64.getEncoder().encode(this.secretKey.getBytes())).parseClaimsJws(token)
					.getBody();
		} else {
			return Jwts.parser().setSigningKey(Base64.getEncoder().encode(this.secretKey.getBytes()))
					.parseClaimsJws(rawtoken).getBody();
		}
	}

	// code to check if token is valid
	public boolean isValidToken(String token) {
		return getClaims(token).getExpiration().after(new Date(System.currentTimeMillis()));
	}

	// check token type
	public String getTokenType(String rawtoken) {
		if (rawtoken.startsWith(AuthConstantStore.TOKEN_PREFIX)) {
			final String token = rawtoken.substring(7, rawtoken.length());
			return Jwts.parser().setSigningKey(Base64.getEncoder().encode(this.secretKey.getBytes())).parseClaimsJws(token)
					.getHeader().getType();
		} else {
			return Jwts.parser().setSigningKey(Base64.getEncoder().encode(this.secretKey.getBytes()))
					.parseClaimsJws(rawtoken).getHeader().getType();
		}
	}
	
	// code to check if token is valid as per username
	public boolean isValidToken(String token, String username) {
		final String tokenUserName = getSubject(token);
		return (username.equals(tokenUserName) && !isTokenExpired(token));
	}

	// code to check if token is expired
	public boolean isTokenExpired(String token) {
		return getExpirationDate(token).before(new Date(System.currentTimeMillis()));
	}

	// code to get expiration date
	public Date getExpirationDate(String token) {
		return getClaims(token).getExpiration();
	}

	// code to get subject
	public String getSubject(String token) {
		return getClaims(token).getSubject();
	}
}
