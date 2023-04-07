package com.concertidc.mcqtest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.model.AnswerSheet;
import com.concertidc.mcqtest.service.McqService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	McqService mcqService;

	@GetMapping("/display-questions")
	public List<QuestionsDto> displayQuestions() {
		return mcqService.displayQuestions();
	}

	@PostMapping("/write-exam/{question-number}")
	public ResponseEntity<?> writeExam(HttpServletRequest request, @PathVariable("question-number") Long questionNumber,
			@RequestBody AnswerSheet answerSheet) {
		return mcqService.writeExam(request, questionNumber, answerSheet);
	}

	@GetMapping("/display-result")
	public String displayResult(HttpServletRequest request) throws Exception {
		return mcqService.displayResult(request);
	}

}
