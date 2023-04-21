package com.concertidc.mcqtest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import com.concertidc.mcqtest.dto.MarksFilter;
import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.DepartmentRepository;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.service.McqService;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;
import com.concertidc.mcqtest.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private JwtUtils jwtUtils;

	@Mock
	private AdminController adminController;

	@Mock
	private UserDetailServiceImpl userDetailServiceImpl;

	@Mock
	private McqService mcqService;

	@MockBean
	private UsersRepository userRepository;

	@MockBean
	private QuestionsRepository questionsRepository;

	@MockBean
	private DepartmentRepository departmentRepository;

	Department department;
	Users users;
	AnswerKey answerKey;
	Questions questions;
	Options options;
	Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
	String token;

	@BeforeEach
	void setUp() {

		Set<String> roles = new HashSet<>();
		roles.add("Admin");

		department = new Department("EE6503", "Electrical");

		users = new Users((long) 1, "admin", "Gokul", "D", "password", department, "Paramathi", roles);

		answerKey = new AnswerKey((long) 1, "a");

		options = new Options((long) 1, "a", "b", "c", "d");

		questions = new Questions((long) 1, "Odd One Out!", options, answerKey);

		Set<String> newRoles = users.getRoles();

		for (String newRole : newRoles) {
			grantedAuthorities.add(new SimpleGrantedAuthority(newRole));
		}

		token = jwtUtils.generateToken(users.getUsername());

	}

	@AfterEach
	void tearDown() {

		users = null;
		questions = null;
		department = null;

	}

	/*
	 * Test Case for Saving User Details in the Database, Authorized only for Admin
	 * Roles, Given User data and Returned Status Code and Status Message
	 */
	@Test
	void testSaveUser() throws Exception {

		List<Department> departmentList = new ArrayList<>();
		departmentList.add(department);

		when(userRepository.save(any(Users.class))).thenReturn(users);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(departmentList);

		ResultActions testResponse = mockMvc.perform(post("/admin/save-user").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(users)));

		assertEquals(201, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("User Saved in the DataBase"));
	}

	/*
	 * Test Case for throwing Invalid department code error when an User tried to
	 * save with Department code which is not Registered, Testing the Exception
	 * which is being handled or not, User data was Given with invalid Department code
	 * and returned Exception
	 */
	@Test
	void testInvalidDepartmentCodeWhileSaveUser() throws Exception {

		List<Department> emptyDepartmentList = new ArrayList<>();

		when(userRepository.save(any(Users.class))).thenReturn(users);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(emptyDepartmentList);

		ResultActions testResponse = mockMvc.perform(post("/admin/save-user").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(users)));

		assertEquals(400, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Department Code is Invalid"));

	}

	/*
	 * Test Case for checking the Create Questions Endpoints which actually
	 * returning the correct status code and message, Autherized only for Admin
	 * Roles, Given Question List and Returned status code and response message.
	 */
	@Test
	void testCreateQuestions() throws Exception {

		List<Questions> questionList = new ArrayList<>();
		List<Questions> emptyList = new ArrayList<>();

		questionList.add(questions);
		questionList.add(questions);

		when(questionsRepository.findAll()).thenReturn(emptyList);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(questionsRepository.saveAll(anyList())).thenReturn(questionList);

		ResultActions testResponse = mockMvc.perform(post("/admin/create-questions").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(questionList)));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Questions Saved to the Database"));
	}

	/*
	 * Checking If Exception thrown When there are Questions already in the
	 * Database, Returning Question data when Repository method is called to invoke
	 * the Exception message.
	 */
	@Test
	void testQuestionsIsPresentErrorForNewQuestions() throws Exception {

		List<Questions> questionList = new ArrayList<>();
		questionList.add(questions);

		when(questionsRepository.findAll()).thenReturn(questionList);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc.perform(post("/admin/create-questions").header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(questionList)));
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Reset Application Before Adding new Set of Questions"));
	}

	/*
	 * Checking the endpoints for updating the department list whether it is
	 * returning the expected status code and response message.
	 */
	@Test
	void testUpdateDepartMentList() throws Exception {

		Department newDepartment = new Department("ME7503", "Mechanical");
		List<Department> emptyList = new ArrayList<>();

		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(emptyList);
		when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc
				.perform(post("/admin/update-department-list").header("Authorization", token)
						.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(newDepartment)));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Department with code ME7503 saved successfully"));
	}

	/*
	 * While Saving a Department in the Department List, Checking the depertment is
	 * present in the department List or not, This test case is actually testing the
	 * exception message thrown when a user tried to save a department which is
	 * already present in the Department List.
	 */
	@Test
	void testErrorWhileSavingNewDepartment() throws Exception {

		List<Department> departmentList = new ArrayList<>();
		departmentList.add(new Department("EE6503", "Electircal"));

		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(departmentList);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc
				.perform(post("/admin/update-department-list").header("Authorization", token)
						.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(department)));

		assertEquals(400, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("Department Already Exists"));
	}

	/*
	 * Testing the Mark list endpoint which shows actual list of Users with their
	 * marks and their results and in this test the actual mark list is given and
	 * returned back to test the status code and the User List.
	 */
	@Test
	void testMarkListDetails() throws Exception {

		MarksFilter marksFilter = new MarksFilter();
		List<MarksFilter> marksFilterList = new ArrayList<>();
		List<UsersDto> passedUser = new ArrayList<>();
		List<UsersDto> failedUser = new ArrayList<>();

		UsersDto testUser1 = new UsersDto(1L, "Gokul", "D", 10, 7);
		UsersDto testUser2 = new UsersDto(2L, "Prem", "D", 10, 4);

		passedUser.add(testUser1);
		failedUser.add(testUser2);
		marksFilter.setPassedCandidates(passedUser);
		marksFilter.setFailedCandidates(failedUser);
		marksFilterList.add(marksFilter);

		when(mcqService.calculateMarks()).thenReturn(marksFilterList);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));

		ResultActions testResponse = mockMvc.perform(get("/admin/all-user-marks").header("Authorization", token));

		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("passedCandidates"));
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("failedCandidates"));

	}
}
