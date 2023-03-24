package com.concertidc.mcqtest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.entity.AnswerKey;
import com.concertidc.mcqtest.entity.Questions;
import com.concertidc.mcqtest.entity.Users;
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
	@PostMapping("/answers/{userId}")
	public Users writeExam(@PathVariable("userId")Long id, @RequestBody Map<String, String> answerMap)
	{
		return mcqService.writeExam(id, answerMap);
	}
	//Display the Result
	@GetMapping("/displayResult/{userId}")
	public String displayResult(@PathVariable("userId")Long id)
	{
		return mcqService.displayResult(id);
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
}
