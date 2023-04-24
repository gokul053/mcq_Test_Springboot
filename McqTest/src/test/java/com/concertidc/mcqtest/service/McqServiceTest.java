package com.concertidc.mcqtest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.concertidc.mcqtest.advice.DuplicateAnswerException;
import com.concertidc.mcqtest.dto.AllUserDto;
import com.concertidc.mcqtest.dto.ResponseMessage;
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

import jakarta.persistence.EntityExistsException;

@SpringBootTest
@Transactional
class McqServiceTest {
	
	@Autowired
	private McqService mcqService;
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private QuestionsRepository questionsRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private AnswerSheetRepository answerSheetRepository;
	
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
	void testCreateQuestionsMethod() {
		
		List<Questions> questionList = new ArrayList<>();
		questionList.add(questions);
		ResponseEntity<ResponseMessage> response = mcqService.createQuestions(questionList);
		assertEquals("200 OK", response.getStatusCode().toString());
		assertEquals("Questions Saved to the Database", response.getBody().getMessage());
		assertFalse(questionsRepository.findAll().isEmpty());
		
	}
	
	@Test
	void testErrorsInCreateQuestionsMethod() {
		
		List<Questions> questionList = new ArrayList<>();
		questionList.add(questions);
		questionsRepository.save(questions);
		ResponseEntity<ResponseMessage> response = mcqService.createQuestions(questionList);
		assertEquals("200 OK", response.getStatusCode().toString());
		assertEquals("Reset Application Before Adding new Set of Questions", response.getBody().getMessage());
		assertFalse(questionsRepository.findAll().isEmpty());
		
	}
	
	@Test
	void testWriteExamMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		questionsRepository.save(questions);
		String token = jwtUtils.generateToken(users.getUsername());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		List<UserAnswers> userAnswerList = new ArrayList<>();
		userAnswerList.add(userAnswers);
		ResponseEntity<ResponseMessage> response = mcqService.writeExam(request, userAnswerList);
		assertEquals("200 OK", response.getStatusCode().toString());
		assertEquals("Answer Saved to the Database", response.getBody().getMessage());
		
	}
	
	@Test
	void testErrorsInWriteExamMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		questionsRepository.save(questions);
		String token = jwtUtils.generateToken(users.getUsername());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		Users testUser = usersRepository.findByUsername(users.getUsername()).orElseThrow();
		userAnswers = new UserAnswers(1L, "a", questions, testUser);
		List<UserAnswers> userAnswerList = new ArrayList<>();
		userAnswerList.add(userAnswers);
		answerSheetRepository.saveAll(userAnswerList);
		assertThrows(DuplicateAnswerException.class, () -> mcqService.writeExam(request, userAnswerList));
	}
	
	@Test
	void testUpdateDepartmentListMethod() {
		
		department = new Department("HS6565", "History");
		String response = mcqService.updateDepartmentList(department);
		assertEquals("HS6565", response);
		
	}
	
	@Test
	void testErrorInUpdateDepartmentListMethod() {
		
		departmentRepository.save(department);
		assertThrows(EntityExistsException.class, () -> mcqService.updateDepartmentList(department));
		
	}
	
	@Test
	void testReintializeTestMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		questionsRepository.save(questions);
		String token = jwtUtils.generateToken(users.getUsername());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		Users testUser = usersRepository.findByUsername(users.getUsername()).orElseThrow();
		userAnswers = new UserAnswers(1L, "a", questions, testUser);
		List<UserAnswers> userAnswerList = new ArrayList<>();
		userAnswerList.add(userAnswers);
		answerSheetRepository.saveAll(userAnswerList);
		
		answerSheetRepository.deleteAll();
		questionsRepository.deleteAll();
		
		assertTrue(answerSheetRepository.findAll().isEmpty());
		assertTrue(questionsRepository.findAll().isEmpty());
		
	}
	
	@Test
	void testListAllUsersMethod() {
		
		departmentRepository.save(department);
		usersRepository.save(users);
		questionsRepository.save(questions);
		String token = jwtUtils.generateToken(users.getUsername());
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", token);
		Users testUser = usersRepository.findByUsername(users.getUsername()).orElseThrow();
		userAnswers = new UserAnswers(1L, "a", questions, testUser);
		List<UserAnswers> userAnswerList = new ArrayList<>();
		userAnswerList.add(userAnswers);
		answerSheetRepository.saveAll(userAnswerList);
		
		List<AllUserDto>userList = mcqService.listAllUsers();
		
		assertFalse(userList.isEmpty());
		userList.forEach(user -> assertEquals("admin", user.getUsername()));
		userList.forEach(user -> assertEquals("Gokul", user.getFirstName()));
	
	}
}
