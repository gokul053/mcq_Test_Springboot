package com.concertidc.mcqtest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.config.EndPointStore;
import com.concertidc.mcqtest.model.UserAnswers;
import com.concertidc.mcqtest.service.McqService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(EndPointStore.USER_ENDPOINT)
public class UserController {

	@Autowired
	McqService mcqService;

	@PostMapping(EndPointStore.WRITE_EXAM)
	public ResponseEntity<?> writeExam(HttpServletRequest request, @RequestBody List<UserAnswers> answerSheet) {
		return this.mcqService.writeExam(request, answerSheet);
	}

	@GetMapping(EndPointStore.RESULT)
	public String displayResult(HttpServletRequest request) throws Exception {
		return this.mcqService.displayResult(request);
	}

}
