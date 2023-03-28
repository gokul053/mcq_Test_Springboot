package com.concertidc.mcqtest.service;

import java.util.List;
import java.util.Map;

import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;

public interface McqService {

	String createUsers(Users users);

	List<Users> displayAllUsers();

	Questions createQuestions(Questions questions);

	List<Questions> displayAllQuestions();

	Users writeExam(Long id, Map<String, String> answerMap);

	AnswerKey createAnswerKey(AnswerKey answerkey);

	String displayResult(Long id);

	List<QuestionsDto> showQuestions();

	List<UsersDto> filterMarksAboveSeven();

	List<UsersDto> filterMarksBelowSeven();	
}
