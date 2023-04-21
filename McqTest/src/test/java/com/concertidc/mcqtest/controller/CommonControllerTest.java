package com.concertidc.mcqtest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.concertidc.mcqtest.dto.LoginRequest;
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;
import com.concertidc.mcqtest.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class CommonControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private ObjectMapper mapper;

	@Mock
	private CommonController commonController;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserDetailServiceImpl userDetailServiceImpl;

	@MockBean
	private UsersRepository usersRepository;

	@MockBean
	private QuestionsRepository questionsRepository;

	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;
	String token;
	String refreshToken;

	@BeforeEach
	void setUp() {
		Set<String> roles = new HashSet<>();
		roles.add("Admin");

		department = new Department("EE6503", "Electrical");

		users = new Users((long) 1, "admin", "Gokul", "D",
				"$2a$12$1rk8KgeNa2n.M.MOF0FZQ.yidVp5OZam1v1xpjJKShmjutItb3Tx6", department, "Velur", roles);

		answerKey = new AnswerKey((long) 1, "a");

		options = new Options((long) 1, "a", "b", "c", "d");

		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);

		token = jwtUtils.generateToken(users.getUsername());

		refreshToken = jwtUtils.generateRefreshToken(users.getUsername());
	}

	@AfterEach
	void tearDown() {

		users = null;
		questions = null;

	}

	/*
	 * Test Case for Login API in which Mock User details are given and returned
	 * back to verify the status code and generation of tokens.
	 */
	@Test
	void testLogin() throws Exception {

		LoginRequest request = new LoginRequest("admin", "password");
		LoginResponse expectedResponse = new LoginResponse(users.getUserId(), users.getUsername(), users.getFirstName(),
				users.getLastName(), users.getDepartment().getDepartmentName(), users.getAddress(),
				"ey_example_access_token", "ey_example_refresh_token");

		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(userDetailServiceImpl.login(anyString())).thenReturn(expectedResponse);

		ResultActions actualResponse = mockMvc.perform(post("/home/login-user").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)));

		assertEquals(200, actualResponse.andReturn().getResponse().getStatus());
		assertTrue(actualResponse.andReturn().getResponse().getContentAsString().contains("Gokul"));

	}

	/*
	 * Test case for throwing error when Username provided while logging in was not
	 * found in the database.
	 */
	@Test
	void testUsernameNotFoundWhileLogin() throws Exception {

		LoginRequest request = new LoginRequest("admin", "password");

		ResultActions testResponse = mockMvc.perform(post("/home/login-user").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)));

		assertEquals(400, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Username not found in the Database!"));

	}

	/*
	 * Test case for throwing error when Password does not match with the User
	 * details obtained via username while Logging in.
	 */
	@Test
	void testPasswordErrorWhileLogin() throws Exception {

		LoginRequest request = new LoginRequest("admin", "password123");

		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc.perform(post("/home/login-user").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)));

		assertEquals(400, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Password Error"));
	}

	/*
	 * Test case for Display Questions API whether the API throws the expected
	 * Question List and status code
	 */
	@Test
	void testDisplayQuestions() throws Exception {
		List<Questions> questionList = new ArrayList<>();
		questionList.add(questions);

		when(questionsRepository.findAll()).thenReturn(questionList);

		ResultActions testResponse = mockMvc.perform(get("/home/display-questions"));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Odd One Out!"));
	}

	/*
	 * Test the Error thrown when there is no questions in the database to display
	 * if a user requested the Display Question API.
	 */
	@Test
	void testNoQuestionsFoundForDisplaying() throws Exception {

		List<Questions> emptyList = new ArrayList<>();

		when(questionsRepository.findAll()).thenReturn(emptyList);

		ResultActions testResponse = mockMvc.perform(get("/home/display-questions"));

		assertEquals(404, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Requested data not found, Contact Admin"));

	}

	/*
	 * Testing the Refresh Token endpoint in which the response is present with
	 * expected status and elements which contains userame and a new access token.
	 */
	@Test
	void testRefreshToken() throws Exception {
		
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		
		ResultActions testResponse = mockMvc.perform(get("/home/refresh-token").header("isRefreshToken",refreshToken));
		
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("ey"));
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("newAccessToken"));
	}
}
