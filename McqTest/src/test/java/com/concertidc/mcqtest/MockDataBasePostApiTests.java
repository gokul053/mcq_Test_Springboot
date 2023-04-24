package com.concertidc.mcqtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class MockDataBasePostApiTests {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private QuestionsRepository questionsRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private AnswerSheetRepository answerSheetRepository;

	@Test
	void testSaveUser() {
		
		Set<String> roles = new HashSet<>();
		roles.add("User");
		Department department = new Department("EE6503", "Electrical");
		departmentRepository.save(department);
		Users users = new Users(0L, "testuser", "Gokul", "D", "$2a$12$Z52BjSVI8Fvq3aBWe2NkKOcfZasF.ADNJvHoXsYjnbyHEuyfQxp9O", department, "Velur", roles);
		usersRepository.save(users); 
		Users testUser = usersRepository.findByUsername(users.getUsername()).orElseThrow(null);
		assertEquals("testuser", testUser.getUsername());
		assertEquals("Gokul", testUser.getFirstName());
		
	}
	
	@Test
	void testCreateQuestions() {
		
		AnswerKey answerKey = new AnswerKey((long) 1, "a");
		Options options = new Options((long) 1, "a", "b", "c", "d");
		Questions questions = new Questions((long) 1, "Test Question", options, answerKey);
		questionsRepository.save(questions);
		Questions testQuestion = questionsRepository.findById((long)1).orElseThrow(null);
		assertEquals("Test Question", testQuestion.getQuestion());
		assertEquals("a", testQuestion.getAnswerKey().getAnswerKey());
		
	}
	
	@Test
	void testUpdateDepartment() {
		
		Department department = new Department("CV4545", "Civil");
		departmentRepository.save(department);
		List<Department> testDepartment = departmentRepository.findByDepartmentCode(department.getDepartmentCode());
		assertFalse(testDepartment.isEmpty());
		
	}
	
	@Test
	void testWriteExam() {
		
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
		List<UserAnswers> testUserAnswers = answerSheetRepository.findByUsers(testUser);
		assertFalse(testUserAnswers.isEmpty());
		testUserAnswers.forEach(testUserAnswer -> assertEquals("testuser", testUserAnswer.getUsers().getUsername()));
		
	}
	
}
