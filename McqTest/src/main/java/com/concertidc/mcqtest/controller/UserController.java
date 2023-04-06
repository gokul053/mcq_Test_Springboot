package com.concertidc.mcqtest.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.service.McqServiceImpl;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	McqServiceImpl mcqServiceImpl;

	@GetMapping("/display-questions")
	public List<QuestionsDto> displayQuestions() {
		return mcqServiceImpl.displayQuestions();
	}

	@PostMapping("/write-exam/{question-number}")
	public ResponseEntity<?> writeExam(Principal principal, @PathVariable("question-number") Long questionNumber,
			@RequestBody Map<String, String> answerMap) {
		return mcqServiceImpl.writeExam(principal, questionNumber, answerMap);
	}

	@GetMapping("/display-result")
	public String displayResult(Principal principal) throws Exception {
		return mcqServiceImpl.displayResult(principal);
	}

}
