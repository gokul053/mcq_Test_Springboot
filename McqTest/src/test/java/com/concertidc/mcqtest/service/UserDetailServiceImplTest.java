package com.concertidc.mcqtest.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.concertidc.mcqtest.advice.InputFormatErrorException;
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.dto.RefreshTokenResponse;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.UserAnswers;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.DepartmentRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.utils.JwtUtils;

@SpringBootTest
@Transactional
class UserDetailServiceImplTest {
	
	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private JwtUtils jwtUtils;
	
	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;
	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
	UserAnswers userAnswers;
	String token;
	
	@BeforeEach
	void setUp() {
		
		Set<String> roles = new HashSet<>();
		roles.add("User");

		department = new Department("EE6503", "Electrical");

		users = new Users((long) 1, "admin", "Gokul", "D", "$2a$12$gH2.Jx1CnTW.UbI7ukV4EupJksQs0bd4Uc/wsUzK1zbu9atOoyxAi", department, "Paramathi", roles);

		answerKey = new AnswerKey((long) 1, "a");

		options = new Options((long) 1, "a", "b", "c", "d");

		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);
		
		userAnswers = new UserAnswers(1L, "a", questions, users);
		
	}
	
	@Test
	void testLoadUserByUsernameMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		
		UserDetails response = userDetailServiceImpl.loadUserByUsername(users.getUsername());
		
		assertEquals("admin", response.getUsername());
		assertEquals("[User]", response.getAuthorities().toString());
		assertEquals("$2a$12$gH2.Jx1CnTW.UbI7ukV4EupJksQs0bd4Uc/wsUzK1zbu9atOoyxAi", response.getPassword());
	}
	
	@Test 
	void testErrorInLoadUserByUsernameMethod() {

		assertThrows(UsernameNotFoundException.class, () -> userDetailServiceImpl.loadUserByUsername(users.getUsername()));
		
	}
	
	@Test
	void testLoginMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		
		LoginResponse response = userDetailServiceImpl.login(users.getUsername());
		
		assertEquals("admin", response.getUsername());
		assertEquals("Gokul", response.getFirstName());
		assertEquals("D", response.getLastName());
		assertEquals("Electrical", response.getDepartment());
		assertFalse(response.getAccessToken().isEmpty());
		assertFalse(response.getRefreshToken().isEmpty());
	}
	
	@Test
	void testErrorInLoginMethod() {
		
		assertThrows(UsernameNotFoundException.class, () -> userDetailServiceImpl.login(users.getUsername()));
		
	}
	
	@Test
	void testGenerateNewAccessTokenMethod() {
		
		String token = jwtUtils.generateRefreshToken(users.getUsername());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("isRefreshToken", token);
		RefreshTokenResponse response = userDetailServiceImpl.generateNewAccessToken(request);
		
		assertEquals("admin", response.getUsername());
		assertFalse(response.getNewAccessToken().isEmpty());
	}
	
	@Test
	void testValidateCredentialsMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		
		String username = "admin";
		String password = "password";
		
		assertDoesNotThrow(() ->userDetailServiceImpl.validateCredentials(username, password));
		
	}
	
	@Test
	void testFormatErrorInValidateCredentialsMethod() {
		
		String username = null;
		String password = "password";
		
		assertThrows(InputFormatErrorException.class, () -> userDetailServiceImpl.validateCredentials(username, password));
		
	}
	
	@Test
	void testErrorInValidateCredentialsMethod() {
		
		String username = "admin";
		String password = "password";
		
		assertThrows(UsernameNotFoundException.class, () -> userDetailServiceImpl.validateCredentials(username, password));
	}

}
