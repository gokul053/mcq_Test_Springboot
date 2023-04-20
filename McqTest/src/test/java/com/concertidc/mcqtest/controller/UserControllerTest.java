package com.concertidc.mcqtest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.UserAnswers;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.AnswerSheetRepository;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.service.McqService;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;
import com.concertidc.mcqtest.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Mock
	private UserController userController;

	@Mock
	private McqService mcqService;

	@Mock
	private UserDetailServiceImpl userDetailServiceImpl;

	@MockBean
	private JwtUtils jwtUtils;

	@MockBean
	private UsersRepository userRepository;

	@MockBean
	private AnswerSheetRepository answerSheetRepository;

	@MockBean
	private QuestionsRepository questionsRepository;

	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;
	UserAnswers userAnswers;
	final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

	@BeforeEach
	void setUp() {
		Set<String> roles = new HashSet<>();
		roles.add("User");
		department = new Department("EE6503", "Electrical");
		users = new Users((long) 1, "admin", "Gokul", "D", "password", department, "Paramathi", roles);

		answerKey = new AnswerKey((long) 1, "a");
		options = new Options((long) 1, "a", "b", "c", "d");
		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);

		final Set<String> newRoles = users.getRoles();
		for (String newRole : newRoles) {
			grantedAuthorities.add(new SimpleGrantedAuthority(newRole));
		}

		userAnswers = new UserAnswers(1L, "a", questions, users);

	}

	@AfterEach
	void tearDown() {
		users = null;
		questions = null;
		department = null;
		userAnswers = null;
	}

	@Test
	void testWriteExam() throws Exception {

		List<UserAnswers> mockAnswerList = new ArrayList<>();
		mockAnswerList.add(userAnswers);
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(answerSheetRepository.save(any(UserAnswers.class))).thenReturn(userAnswers);
		when(questionsRepository.findById(anyLong())).thenReturn(Optional.of(questions));
		
		when(userDetailServiceImpl.loadUserByUsername(anyString()))
				.thenReturn(new User(users.getUsername(), users.getPassword(), grantedAuthorities));
		when(jwtUtils.isTokenExpired(anyString())).thenReturn(false);
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.isValidToken(anyString())).thenReturn(true);
		when(jwtUtils.isValidToken(anyString(), anyString())).thenReturn(true);
		
		ResultActions testResponse = mockMvc.perform(post("/user/write-exam").header("Authorization",
				"eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI2OTIiLCJzdWIiOiJhZG1pbiIsImlzcyI6Im1jcXRlc3QuY29tIiwiYXVkIjoic3R1ZGVudHMiLCJpYXQiOjE2ODE5MDEwNDYsImV4cCI6MTY4MTkwMTM0Nn0.IQrFibEtnweCry-ZKDI9j1TM2aFkAinMPNNJxQz6ymtsjrEhCVZ7PPY2EtCJ4Eac98TdXBGApGoDMmbE2_6uSA")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockAnswerList)));
		
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(
				testResponse.andReturn().getResponse().getContentAsString().contains("Answer Saved to the Database"));
	}

	
	@Test
	void testDisplayResult() throws Exception {
		
		List<UserAnswers> userAnswerList = new ArrayList<>();
		List<Questions> questionList = new ArrayList<>();
		for(int i = 1; i<=10; i++) {
			questionList.add(new Questions((long) i, "Odd One Out!", options, answerKey));
		}
		for(int i = 1; i<=10; i++) {
			userAnswerList.add(new UserAnswers((long) i, "a", questions, users));
		}
		when(answerSheetRepository.findByUsers(any(Users.class))).thenReturn(userAnswerList);
		when(questionsRepository.findAll()).thenReturn(questionList);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(userDetailServiceImpl.loadUserByUsername(anyString()))
		.thenReturn(new User(users.getUsername(), users.getPassword(), grantedAuthorities));
		when(jwtUtils.isTokenExpired(anyString())).thenReturn(false);
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.isValidToken(anyString())).thenReturn(true);
		when(jwtUtils.isValidToken(anyString(), anyString())).thenReturn(true);

		ResultActions testResponse = mockMvc.perform(get("/user/display-result").header("Authorization",
		"eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI2OTIiLCJzdWIiOiJhZG1pbiIsImlzcyI6Im1jcXRlc3QuY29tIiwiYXVkIjoic3R1ZGVudHMiLCJpYXQiOjE2ODE5MDEwNDYsImV4cCI6MTY4MTkwMTM0Nn0.IQrFibEtnweCry-ZKDI9j1TM2aFkAinMPNNJxQz6ymtsjrEhCVZ7PPY2EtCJ4Eac98TdXBGApGoDMmbE2_6uSA"));
		
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(
				testResponse.andReturn().getResponse().getContentAsString().contains("Marks : 10, You're Pass"));
	}
}
