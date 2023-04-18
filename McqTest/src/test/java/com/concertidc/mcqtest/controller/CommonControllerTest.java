package com.concertidc.mcqtest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.concertidc.mcqtest.dto.LoginRequest;
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.dto.RefreshTokenResponse;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;
import com.concertidc.mcqtest.utils.JwtUtils;

@SpringBootTest
@AutoConfigureMockMvc
class CommonControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@InjectMocks
	CommonController commonController;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	UserDetailServiceImpl userDetailServiceImpl;
	
	@MockBean
	JwtUtils jwtUtils;
	
	@MockBean
	QuestionsRepository questionsRepository;
	

	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;

	@BeforeEach
	void setUp() {
		Set<String> roles = new HashSet<>();
		roles.add("Admin");
		department = new Department("EE6503", "Electrical");
		users = new Users((long) 1, "admin", "Gokul", "D", "password", department, "Velur", roles);
		
		answerKey = new AnswerKey((long)1, "a");
		options = new Options((long) 1, "a", "b", "c", "d");
		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);
	}

	@AfterEach
	void tearDown() {
		users = null;
		questions = null;
	}

	@Test
	void testLogin() throws Exception {
		
		LoginRequest request = new LoginRequest("admin", "password");
		LoginResponse expectedResponse = 
		new LoginResponse(
				users.getUserId(), 
				users.getUsername(), 
				users.getFirstName(),
				users.getLastName(),
				users.getDepartment().getDepartmentName(),
				users.getAddress(), 
				"ey_example_access_token",
				"ey_example_refresh_token");
		
		when(userDetailServiceImpl.findByUsername("admin")).thenReturn(Optional.of(users));
		when(userDetailServiceImpl.login("admin")).thenReturn(expectedResponse);
		ResponseEntity<?> actualResponse = commonController.login(request);
		assertEquals("200 OK", actualResponse.getStatusCode().toString());
		assertEquals(expectedResponse, actualResponse.getBody());
	}
	
	@Test 
	void testDisplayQuestions() throws Exception {
		List<Questions> questionList = new ArrayList<>();
		questionList.add(questions);
		when(questionsRepository.findAll()).thenReturn(questionList);
		ResultActions testResponse = mockMvc.perform(get("/home/display-questions"));
		testResponse
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}
	
	@Test
	void testRefreshToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("isRefreshToken", "ey_refresh_token");
		RefreshTokenResponse response = new RefreshTokenResponse("admin", "ey_new_access_token");
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.generateToken(anyString())).thenReturn("ey_new_access_token");
		when(userDetailServiceImpl.generateNewAccessToken(request)).thenReturn(response);
		ResultActions testResponse = mockMvc.perform(get("/home/refresh-token").header("isRefreshToken", "ey_refresh_token"));
		testResponse
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	}

}
