package com.concertidc.mcqtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

@SpringBootTest
@Transactional
public class MockDataBaseGetApiTests {
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private QuestionsRepository questionsRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private AnswerSheetRepository answerSheetRepository;
	
	@BeforeEach
	void setUp() {
		
		Set<String> roles = new HashSet<>();
		roles.add("User");
		Department department = new Department("EE6503", "Electrical");
		departmentRepository.save(department);
		Users users = new Users(0L, "testuser", "Gokul", "D", "$2a$12$Z52BjSVI8Fvq3aBWe2NkKOcfZasF.ADNJvHoXsYjnbyHEuyfQxp9O", department, "Velur", roles);
		usersRepository.save(users).getUserId();
		Users testUser = usersRepository.findByUsername("testuser").orElseThrow(null); 
		AnswerKey answerKey = new AnswerKey((long) 1, "a");
		Options options = new Options((long) 1, "a", "b", "c", "d");
		Questions questions = new Questions((long) 1, "Test Question", options, answerKey);
		questionsRepository.save(questions);
		UserAnswers userAnswers = new UserAnswers(1L, "a", questions, testUser);
		answerSheetRepository.save(userAnswers);
		
	}
	
	@Test
	void testAllUserList() {
		
		List<Users> userList = usersRepository.findAll();
		userList.forEach(user -> assertEquals("Gokul", user.getFirstName()));
		userList.forEach(user -> assertEquals("testuser", user.getUsername()));
		userList.forEach(user -> assertEquals("Velur", user.getAddress()));
		
	}
	
	@Test
	void testAllQuestionsList() {
		
		List<Questions> questionList = questionsRepository.findAll();
		questionList.forEach(question -> assertEquals("Test Question", question.getQuestion()));
		questionList.forEach(question -> assertEquals("a", question.getAnswerKey().getAnswerKey()));
		
	}

}
