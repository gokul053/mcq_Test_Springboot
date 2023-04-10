package com.concertidc.mcqtest.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.config.EndPointStore;
import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.service.McqService;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;

@RestController
@RequestMapping(EndPointStore.ADMIN_ENDPOINT)
public class AdminController {

	@Autowired
	private McqService mcqService;

	@Autowired
	public UserDetailServiceImpl userDetailServiceImpl;

	@PostMapping(EndPointStore.UPDATE_DEPARTMENT_LIST)
	public ResponseEntity<?> updateDepartmentList(@RequestBody Department department) {
		final String code = this.mcqService.updateDepartmentList(department);
		final String message = "Department with code " + code + " saved successfully";
		return ResponseEntity.ok(message);
	}

	@PostMapping(EndPointStore.SAVE_USER)
	public ResponseEntity<?> saveUser(@RequestBody Users user) throws SQLException {
		return this.userDetailServiceImpl.saveUser(user);
	}

	@PostMapping(EndPointStore.CREATE_QUESTIONS)
	public ResponseEntity<?> createQuestions(@RequestBody List<Questions> questions) {
		return this.mcqService.createQuestions(questions);
	}

	@GetMapping(EndPointStore.DISPLAY_QUESTIONS)
	public List<QuestionsDto> displayQuestions() {
		return this.mcqService.displayQuestions();
	}

	@GetMapping(EndPointStore.USER_MARKS)
	public ResponseEntity<?> calculateMarks() {
		return ResponseEntity.ok(this.mcqService.calculateMarks());
	}

}
