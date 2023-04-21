package com.concertidc.mcqtest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.UsersRepository;

@SpringBootTest
@AutoConfigureMockMvc
class McqTestApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private UsersRepository usersRepository;
	
	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;
	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
	String token;
	Set<String> roles = new HashSet<>();
	
	@BeforeEach
	void setUp() {


		department = new Department("EE6503", "Electrical");

		users = new Users((long) 1, "admin", "Gokul", "D", "password", department, "Paramathi", roles);

		answerKey = new AnswerKey((long) 1, "a");

		options = new Options((long) 1, "a", "b", "c", "d");

		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);

	}

	@AfterEach
	void tearDown() {

		users = null;
		questions = null;
		department = null;

	}
	
	
	@Test
	void testAuthorizationRoles() throws Exception {
		
		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/all-user-marks").header("Authorization", token));
		
		testResponse.andDo(print());
		
	}

}
