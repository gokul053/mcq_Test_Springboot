package com.concertidc.mcqtest.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.service.McqService;

@RestController
public class McqController {
	
	@Autowired
	McqService mcqService;
	
	//Creating an User account
	@PostMapping("/createUsers")
	public String createUsers(@RequestBody Users user)
	{
		return mcqService.createUsers(user);
	}
	//Display the Question Paper
	@GetMapping("/showQuestions")
	public List<QuestionsDto> showQuestions()
	{
		return mcqService.showQuestions();
	}
	//Attend the Exam
	@PostMapping("/answers")
	public Users writeExam(Principal p, @RequestBody Map<String, String> answerMap)
	{
		return mcqService.writeExam(p, answerMap);
	}
	//Display the Result
	@GetMapping("/displayResult")
	public String displayResult(Principal p)
	{
		return mcqService.displayResult(p);
	}
	//Display all Users and their Answers
	@GetMapping("/displayAllUsers")
	public List<Users> displayAllUsers()
	{
		return mcqService.displayAllUsers();
	}
	//Feed Questions and Options to the DataBase
	@PostMapping("/createQuestions")
	public Questions createQuestions(@RequestBody Questions questions)
	{
		return mcqService.createQuestions(questions);
	}
	//Display All the Questions
	@GetMapping("/displayAllQuestions")
	public List<Questions> displayAllQuestions()
	{
		return mcqService.displayAllQuestions();
	}
	//Creating AnswerKey for the Questions
	@PostMapping("/createAnswerKey")
	public AnswerKey createAnswerKey(@RequestBody AnswerKey answerkey)
	{
		return mcqService.createAnswerKey(answerkey);
	}
	//Mark Filters
	@GetMapping("/marksAboveSeven")
	public List<UsersDto> filterMarksAboveSeven()
	{
		return mcqService.filterMarksAboveSeven();
	}
	
	@GetMapping("/marksBelowSeven")
	public List<UsersDto> filterMarksBelowSeven()
	{
		return mcqService.filterMarksBelowSeven();
	}
}

