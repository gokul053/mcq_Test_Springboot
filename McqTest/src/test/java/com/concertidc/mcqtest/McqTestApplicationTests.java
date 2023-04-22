package com.concertidc.mcqtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.UserAnswers;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.AnswerSheetRepository;
import com.concertidc.mcqtest.repository.DepartmentRepository;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class McqTestApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@MockBean
	private UsersRepository usersRepository;
	
	@MockBean
	private DepartmentRepository departmentRepository;
	
	@MockBean
	private QuestionsRepository questionsRepository;
	
	@MockBean
	private AnswerSheetRepository answerSheetRepository;
	
	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;
	UserAnswers userAnswers;
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
		
		userAnswers = new UserAnswers(1L, "a", questions, users);

		token = jwtUtils.generateToken(users.getUsername());
	}

	@AfterEach
	void tearDown() {

		users = null;
		questions = null;
		department = null;

	}
	
	/*
	 * Authorization test based on roles for Save User EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesSaveUserEndpointSuccess() throws Exception {
		
		roles.add("Admin");
		
		List<Questions> questionList = new ArrayList<>();
		List<Questions> emptyList = new ArrayList<>();

		questionList.add(questions);
		questionList.add(questions);

		when(questionsRepository.findAll()).thenReturn(emptyList);
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(questionsRepository.saveAll(anyList())).thenReturn(questionList);

		ResultActions testResponse = mockMvc.perform(post("/admin/create-questions").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(questionList)));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Questions Saved to the Database"));
	}
	
	/*
	 * Authorization test based on roles for Create Questions EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesCreateQuestionsEndpointSuccess() throws Exception {

		roles.add("Admin");
		
		List<Questions> questionList = new ArrayList<>();
		List<Questions> emptyList = new ArrayList<>();

		questionList.add(questions);
		questionList.add(questions);

		when(questionsRepository.findAll()).thenReturn(emptyList);
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(questionsRepository.saveAll(anyList())).thenReturn(questionList);

		ResultActions testResponse = mockMvc.perform(post("/admin/create-questions").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(questionList)));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Questions Saved to the Database"));

		
	}
	
	/*
	 * Authorization test based on roles for Department List EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesDeptListEndPointSuccess() throws Exception {

		roles.add("Admin");
		
		Department newDepartment = new Department("ME7503", "Mechanical");
		List<Department> emptyList = new ArrayList<>();
		
		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(emptyList);
		when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(post("/admin/update-department-list").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(newDepartment)));
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for get Mark List EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesMarkListEndPointSuccess() throws Exception {

		roles.add("Admin");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/all-user-marks").header("Authorization", token));
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for All User List EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesUserListEndPointSuccess() throws Exception {

		roles.add("Admin");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/list-all-users").header("Authorization", token));
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for Reset User Test EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesResetEndPointSuccess() throws Exception {

		roles.add("Admin");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/list-all-users").header("Authorization", token));
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
	}
	
	/*
	 * Authorization test based on roles for Save User EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesSaveUserEndpoint() throws Exception {
		
		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(post("/admin/save-user").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());

	}
	
	/*
	 * Authorization test based on roles for Create Questions EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesCreateQuestionsEndpoint() throws Exception {

		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(post("/admin/create-questions").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for update Department List EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesDeptListEndPoint() throws Exception {

		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(post("/admin/update-department-list").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for Users Marks List EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesMarkListEndPoint() throws Exception {

		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/all-user-marks").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for All User List EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesUserListEndPoint() throws Exception {

		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/list-all-users").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for Reset Test Data EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesResetEndPoint() throws Exception {

		roles.add("User");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/admin/list-all-users").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
	}
	
	/*
	 * Authorization test based on roles for Write Exam EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesWriteExamEndpoint() throws Exception {
		
		roles.add("Admin");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(post("/user/write-exam").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for Display Result EndPoint in a Negative Scenario.
	 */
	@Test
	void testAuthorizationRolesDisplayResultEndpoint() throws Exception {
		
		roles.add("Admin");
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		ResultActions testResponse = mockMvc.perform(get("/user/display-result").header("Authorization", token));
		assertEquals(403, testResponse.andReturn().getResponse().getStatus());
		
	}
	
	/*
	 * Authorization test based on roles for Write Exam EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesWriteExamEndpointSuccess() throws Exception {
		
		roles.add("User");
		List<UserAnswers> mockAnswerList = new ArrayList<>();
		mockAnswerList.add(userAnswers);

		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(answerSheetRepository.save(any(UserAnswers.class))).thenReturn(userAnswers);
		when(questionsRepository.findById(anyLong())).thenReturn(Optional.of(questions));

		ResultActions testResponse = mockMvc.perform(post("/user/write-exam").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(mockAnswerList)));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(
				testResponse.andReturn().getResponse().getContentAsString().contains("Answer Saved to the Database"));
		
	}
	
	/*
	 * Authorization test based on roles for Display Result EndPoint in a Positive Scenario.
	 */
	@Test
	void testAuthorizationRolesDisplayResultEndpointSuccess() throws Exception {
		
		roles.add("User");
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
		when(usersRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc.perform(get("/user/display-result").header("Authorization", token));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Marks : 10, You're Pass"));
		
	}
}
