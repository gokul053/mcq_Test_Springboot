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

import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.service.McqServiceImpl;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;

import jakarta.validation.ConstraintViolationException;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private McqServiceImpl mcqServiceImpl;

	@Autowired
	public UserDetailServiceImpl userDetailServiceImpl;

	@PostMapping("/update-department-list")
	public ResponseEntity<?> updateDepartmentList(@RequestBody Department department) {
		String code = mcqServiceImpl.updateDepartmentList(department);
		String message = "Department with code " + code + " saved successfully";
		return ResponseEntity.ok(message);
	}

	@PostMapping("/save-user")
	public ResponseEntity<?> saveUser(@RequestBody Users user) throws SQLException {
		return userDetailServiceImpl.saveUser(user);
	}

	@PostMapping("/create-questions")
	public ResponseEntity<?> createQuestions(@RequestBody Questions questions) throws ConstraintViolationException {
		return mcqServiceImpl.createQuestions(questions);
	}

	@PostMapping("/create-answerkey")
	public AnswerKey createAnswerKey(@RequestBody AnswerKey answerkey) {
		return mcqServiceImpl.createAnswerKey(answerkey);
	}

	@GetMapping("/marks-above-seven")
	public List<UsersDto> filterMarksAboveSeven() {
		return mcqServiceImpl.filterMarksAboveSeven();
	}

	@GetMapping("/marks-below-seven")
	public List<UsersDto> filterMarksBelowSeven() {
		return mcqServiceImpl.filterMarksBelowSeven();
	}

}
