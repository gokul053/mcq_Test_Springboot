package com.concertidc.mcqtest.service;

import java.security.Principal;
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

	Users writeExam(Principal p, Map<String, String> answerMap);

	AnswerKey createAnswerKey(AnswerKey answerkey);

	String displayResult(Principal p);

	List<QuestionsDto> showQuestions();

	List<UsersDto> filterMarksAboveSeven();

	List<UsersDto> filterMarksBelowSeven();	
}
