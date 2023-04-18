package com.concertidc.mcqtest.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Options;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.service.McqService;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

	@Mock
	McqService mcqService;
	
	@MockBean
	UserDetailServiceImpl userDetailServiceImpl;
	
	@InjectMocks
	AdminController adminController;
	
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
	void testSaveUser() {
		
		
	}

}
