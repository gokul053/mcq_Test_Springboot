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

	@Autowired
	private JwtUtils jwtUtils;

	@Mock
	private UserController userController;

	@Mock
	private McqService mcqService;

	@Mock
	private UserDetailServiceImpl userDetailServiceImpl;

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
	String token;
	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

	@BeforeEach
	void setUp() {

		Set<String> roles = new HashSet<>();
		roles.add("User");

		department = new Department("EE6503", "Electrical");

		users = new Users((long) 1, "admin", "Gokul", "D", "password", department, "Paramathi", roles);

		answerKey = new AnswerKey((long) 1, "a");

		options = new Options((long) 1, "a", "b", "c", "d");

		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);

		Set<String> newRoles = users.getRoles();

		for (String newRole : newRoles) {
			grantedAuthorities.add(new SimpleGrantedAuthority(newRole));
		}

		userAnswers = new UserAnswers(1L, "a", questions, users);

		token = jwtUtils.generateToken(users.getUsername());
	}

	@AfterEach
	void tearDown() {

		users = null;
		questions = null;
		department = null;
		userAnswers = null;

	}

	/*
	 * Test case for Write Exam API in which the User can perform the exam in this
	 * endpoint and the Mock User details are given in order to get respective
	 * response.
	 */
	@Test
	void testWriteExam() throws Exception {

		List<UserAnswers> mockAnswerList = new ArrayList<>();
		mockAnswerList.add(userAnswers);

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(answerSheetRepository.save(any(UserAnswers.class))).thenReturn(userAnswers);
		when(questionsRepository.findById(anyLong())).thenReturn(Optional.of(questions));

		ResultActions testResponse = mockMvc.perform(post("/user/write-exam").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockAnswerList)));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(
				testResponse.andReturn().getResponse().getContentAsString().contains("Answer Saved to the Database"));
	}

	/*
	 * If a User who already answered the question tries to answer again, this
	 * Exception message is thrown which has been verified in this test case.
	 */
	@Test
	void testIfAlreadyAnsweredError() throws Exception {

		List<UserAnswers> mockAnswerList = new ArrayList<>();
		mockAnswerList.add(userAnswers);

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(answerSheetRepository.findByUsers(any(Users.class))).thenReturn(mockAnswerList);

		ResultActions testResponse = mockMvc.perform(post("/user/write-exam").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockAnswerList)));

		assertEquals(406, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("You've Already Answered the Questions"));
	}

	/*
	 * If an Max Question was 10 and a user tries to answer 11th question, an error
	 * will be thrown which is verified in this Test case.
	 */
	@Test
	void testIfInvalidQuestionAnsweredError() throws Exception {

		List<UserAnswers> mockAnswerList = new ArrayList<>();
		mockAnswerList.add(userAnswers);

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(answerSheetRepository.save(any(UserAnswers.class))).thenReturn(userAnswers);
		ResultActions testResponse = mockMvc.perform(post("/user/write-exam").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockAnswerList)));

		assertEquals(400, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Question Number is Invalid"));

	}

	/*
	 * Verifing the Display Result API in which Fetching the user details and
	 * calculating the marks and displaying the result which is verified in this
	 * test case.
	 */
	@Test
	void testDisplayResult() throws Exception {

		List<UserAnswers> userAnswerList = new ArrayList<>();
		List<Questions> questionList = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {
			questionList.add(new Questions((long) i, "Odd One Out!", options, answerKey));
		}
		for (int i = 1; i <= 10; i++) {
			userAnswerList.add(new UserAnswers((long) i, "a", questions, users));
		}

		when(answerSheetRepository.findByUsers(any(Users.class))).thenReturn(userAnswerList);
		when(questionsRepository.findAll()).thenReturn(questionList);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc.perform(get("/user/display-result").header("Authorization", token));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Marks : 10, You're Pass"));
	}
}
