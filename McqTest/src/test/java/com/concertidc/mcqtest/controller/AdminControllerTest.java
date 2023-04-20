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
import org.springframework.security.core.userdetails.User;
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

	@Mock
	private AdminController adminController;

	@MockBean
	private JwtUtils jwtUtils;

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

	}

	@AfterEach
	void tearDown() {
		users = null;
		questions = null;
		department = null;
	}

	/**
	 * 
	 * 
	 *
	 */
	@Test
	void testSaveUser() throws Exception {
		List<Department> departmentList = new ArrayList<>();
		departmentList.add(department);
		when(userRepository.save(any(Users.class))).thenReturn(users);
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(departmentList);
		
		when(userDetailServiceImpl.loadUserByUsername(anyString()))
				.thenReturn(new User(users.getUsername(), users.getPassword(), grantedAuthorities));
		when(jwtUtils.isTokenExpired(anyString())).thenReturn(false);
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.isValidToken(anyString())).thenReturn(true);
		when(jwtUtils.isValidToken(anyString(), anyString())).thenReturn(true);
		
		ResultActions testResponse = mockMvc.perform(post("/admin/save-user").header("Authorization",
				"eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI2OTIiLCJzdWIiOiJhZG1pbiIsImlzcyI6Im1jcXRlc3QuY29tIiwiYXVkIjoic3R1ZGVudHMiLCJpYXQiOjE2ODE5MDEwNDYsImV4cCI6MTY4MTkwMTM0Nn0.IQrFibEtnweCry-ZKDI9j1TM2aFkAinMPNNJxQz6ymtsjrEhCVZ7PPY2EtCJ4Eac98TdXBGApGoDMmbE2_6uSA")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(users)));
		
		assertEquals(201, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString().contains("User Saved in the DataBase"));
	}

	@Test
	void testCreateQuestions() throws Exception {
		List<Questions> questionList = new ArrayList<>();
		List<Questions> emptyList = new ArrayList<>();
		questionList.add(questions);
		when(questionsRepository.findAll()).thenReturn(emptyList);
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(userDetailServiceImpl.loadUserByUsername(anyString()))
				.thenReturn(new User(users.getUsername(), users.getPassword(), grantedAuthorities));
		when(questionsRepository.saveAll(anyList())).thenReturn(questionList);
		when(jwtUtils.isTokenExpired(anyString())).thenReturn(false);
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.isValidToken(anyString())).thenReturn(true);
		when(jwtUtils.isValidToken(anyString(), anyString())).thenReturn(true);
		
		ResultActions testResponse = mockMvc.perform(post("/admin/create-questions").header("Authorization",
				"eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI2OTIiLCJzdWIiOiJhZG1pbiIsImlzcyI6Im1jcXRlc3QuY29tIiwiYXVkIjoic3R1ZGVudHMiLCJpYXQiOjE2ODE5MDEwNDYsImV4cCI6MTY4MTkwMTM0Nn0.IQrFibEtnweCry-ZKDI9j1TM2aFkAinMPNNJxQz6ymtsjrEhCVZ7PPY2EtCJ4Eac98TdXBGApGoDMmbE2_6uSA")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(questionList)));
		
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Questions Saved to the Database"));
	}

	@Test
	void testUpdateDepartMentList() throws Exception {
		Department newDepartment = new Department("ME7503", "Mechanical");
		List<Department> emptyList = new ArrayList<>();
		when(departmentRepository.findByDepartmentCode(anyString())).thenReturn(emptyList);
		when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);
		
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(userDetailServiceImpl.loadUserByUsername(anyString()))
				.thenReturn(new User(users.getUsername(), users.getPassword(), grantedAuthorities));
		when(jwtUtils.isTokenExpired(anyString())).thenReturn(false);
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.isValidToken(anyString())).thenReturn(true);
		when(jwtUtils.isValidToken(anyString(), anyString())).thenReturn(true);
		
		ResultActions testResponse = mockMvc.perform(post("/admin/update-department-list").header("Authorization",
				"eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI2OTIiLCJzdWIiOiJhZG1pbiIsImlzcyI6Im1jcXRlc3QuY29tIiwiYXVkIjoic3R1ZGVudHMiLCJpYXQiOjE2ODE5MDEwNDYsImV4cCI6MTY4MTkwMTM0Nn0.IQrFibEtnweCry-ZKDI9j1TM2aFkAinMPNNJxQz6ymtsjrEhCVZ7PPY2EtCJ4Eac98TdXBGApGoDMmbE2_6uSA")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(newDepartment)));
		assertEquals(200, testResponse.andReturn().getResponse().getStatus());
		assertTrue(testResponse.andReturn().getResponse().getContentAsString()
				.contains("Department with code ME7503 saved successfully"));
	}

	@Test
	void testMarkListDetails() throws Exception {

		UsersDto testUser1 = new UsersDto(1L, "Gokul", "D", 10, 7);
		UsersDto testUser2 = new UsersDto(2L, "Prem", "D", 10, 4);
		List<UsersDto> passedUser = new ArrayList<>();
		passedUser.add(testUser1);
		List<UsersDto> failedUser = new ArrayList<>();
		failedUser.add(testUser2);
		MarksFilter marksFilter = new MarksFilter();
		marksFilter.setPassedCandidates(passedUser);
		marksFilter.setFailedCandidates(failedUser);
		List<MarksFilter> marksFilterList = new ArrayList<>();
		marksFilterList.add(marksFilter);

		when(mcqService.calculateMarks()).thenReturn(marksFilterList);

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(users));
		when(userDetailServiceImpl.loadUserByUsername(anyString()))
				.thenReturn(new User(users.getUsername(), users.getPassword(), grantedAuthorities));
		when(jwtUtils.isTokenExpired(anyString())).thenReturn(false);
		when(jwtUtils.getSubject(anyString())).thenReturn("admin");
		when(jwtUtils.isValidToken(anyString())).thenReturn(true);
		when(jwtUtils.isValidToken(anyString(), anyString())).thenReturn(true);

		ResultActions testResponse = mockMvc.perform(get("/admin/all-user-marks").header("Authorization",
				"eyJ0eXAiOiJBY2Nlc3NUb2tlbiIsImFsZyI6IkhTNTEyIn0.eyJqdGkiOiI2OTIiLCJzdWIiOiJhZG1pbiIsImlzcyI6Im1jcXRlc3QuY29tIiwiYXVkIjoic3R1ZGVudHMiLCJpYXQiOjE2ODE5MDEwNDYsImV4cCI6MTY4MTkwMTM0Nn0.IQrFibEtnweCry-ZKDI9j1TM2aFkAinMPNNJxQz6ymtsjrEhCVZ7PPY2EtCJ4Eac98TdXBGApGoDMmbE2_6uSA"));

		System.out.println(testResponse.andReturn().getResponse().getContentAsString());

	}
}
