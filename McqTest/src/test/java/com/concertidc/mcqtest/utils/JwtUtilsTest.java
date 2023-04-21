package com.concertidc.mcqtest.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@SpringBootTest
@AutoConfigureMockMvc
class JwtUtilsTest {
	
	@Autowired
	private JwtUtils jwtUtils;

	/*
	 * Test Case For Verifing the Token Generation and Verification performs
	 * correctly without error by providing username and generating token
	 * and asserting the values.
	 */
	@Test
	void testGenerateToken() {
		
		String username = "admin";
		String token = jwtUtils.generateToken(username);
		
		assertEquals(jwtUtils.getClaims(token).getSubject(), username);
		assertFalse(jwtUtils.isTokenExpired(token));
		assertTrue(jwtUtils.isValidToken(token));
		assertTrue(jwtUtils.isValidToken(token, username));
		
	}
	
	/*
	 * Testing the GetClaims Method in which the obtained claims are valid 
	 * with respect to expected values which is Hard coded.
	 */
	@Test
	void testGetClaims() {
		
		String username = "admin";
		String tokenIssuer = "mcqtest.com";
		String tokenAudience = "students";
		String token = jwtUtils.generateToken(username);
		
		assertEquals(username, jwtUtils.getClaims(token).getSubject());
		assertEquals(tokenIssuer, jwtUtils.getClaims(token).getIssuer());
		assertEquals(tokenAudience, jwtUtils.getClaims(token).getAudience());
	}
	
	/*
	 * Testing all exceptions occurs when parsing the JWT token
	 * for handling it in a useful way.
	 */
	@Test
	void testTokenExceptions() {
		
		String testTokenExpired = "eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI1MyIsInN1YiI6ImFkbWluIiwiaXNzIjoibWNxdGVzdC5jb20iLCJhdWQiOiJzdHVkZW50cyIsImlhdCI6MTY4MjA3ODQ5NiwiZXhwIjoxNjgyMDc4Nzk2fQ.6lrTmmBThqXhSqc1JBjPBGrr0Tkrg4ztcddLXZVVd49KGyp_77MjtbLNFEn9TDyQdfc-w5XtBGy6oR4mDT4x0w";
		String testTokenSignature = "eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI1MyIsInN1YiI6ImFkbWluIiwiaXNzIjoibWNxdGVzdC5jb20iLCJhdWQiOiJzdHVkZW50cyIsImlhdCI6MTY4MjA3ODQ5NiwiZXhwIjoxNjgyMDc4Nzk2fQ.6lrTmmBThqXhSqc1JBjPBGrr0Tkrg4ztcddLXZVVd49KGyp_77MjtbLNFEn9TDyQdfc-w5XtBGy6R4mDT4x0w";
		String testTokenTampered = "eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI1MyIsInN1YiI6ImFkbWluIiwiaXNzIjoibWNxdGVzdC5jb20iLCJhdWQiOiJzdHVkZW50cyIsImlhdCI6MkY4MjA3ODQ5NiwiZXhwIjoxNjgyMDc4Nzk2fQ.6lrTmmBThqXhSqc1JBjPBGrr0Tkrg4ztcddLXZVVd49KGyp_77MjtbLNFEn9TDyQdfc-w5XtBGy6oR4mDT4x0w";
 		
		assertThrows(ExpiredJwtException.class, () -> jwtUtils.isValidToken(testTokenExpired));
		assertThrows(SignatureException.class, () -> jwtUtils.isValidToken(testTokenSignature));
		assertThrows(MalformedJwtException.class, () -> jwtUtils.isValidToken(testTokenTampered));
	}
	
	/*
	 * Testing whether the token is getting striped while passing 
	 * bearer token field.
	 */
	@Test
	void testBearerTokenParsing() {
		
		String username = "admin";
		String rawToken = jwtUtils.generateToken(username);
		String token = "Bearer " + rawToken;
		
		assertDoesNotThrow(() -> jwtUtils.getClaims(rawToken));
		assertDoesNotThrow(() -> jwtUtils.getClaims(token));
	}
}
