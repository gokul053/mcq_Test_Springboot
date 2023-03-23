package com.concertidc.mcqtest.service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.entity.AnswerKey;
import com.concertidc.mcqtest.entity.AnswerSheet;
import com.concertidc.mcqtest.entity.Questions;
import com.concertidc.mcqtest.entity.Users;
import com.concertidc.mcqtest.repository.AnswerKeyRepository;
import com.concertidc.mcqtest.repository.AnswerSheetRepository;
import com.concertidc.mcqtest.repository.OptionsRepository;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.repository.UsersRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class McqServiceImpl implements McqService {

	@Autowired
	UsersRepository usersRepository;
	@Autowired
	QuestionsRepository questionsRepository;
	@Autowired
	OptionsRepository optionsRepository;
	@Autowired
	AnswerSheetRepository answerSheetRepository;
	@Autowired
	AnswerKeyRepository answerKeyRepository;
	
	@Override
	public String createUsers(Users users) {
		usersRepository.save(users);
		return "Account Created, User Id = " + users.getUserId();
	}

	@Override
	public List<Users> displayAllUsers() {
		return usersRepository.findAll();
	}

	@Override
	public Questions createQuestions(Questions questions) {
		return questionsRepository.save(questions);
	}

	@Override
	public List<Questions> displayAllQuestions() {	
		return questionsRepository.findAll();
	}

	@Override
	public Users writeExam(Long id, Map<String, String> answerMap) throws EntityNotFoundException {
		Users users = usersRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
		AnswerSheet answerSheet = new AnswerSheet();
		answerSheet.setAnswerOne(answerMap.get("answerOne"));
		answerSheet.setAnswerTwo(answerMap.get("answerTwo"));
		answerSheet.setAnswerThree(answerMap.get("answerThree"));
		answerSheet.setAnswerFour(answerMap.get("answerFour"));
		answerSheet.setAnswerFive(answerMap.get("answerFive"));
		answerSheet.setAnswerSix(answerMap.get("answerSix"));
		answerSheet.setAnswerSeven(answerMap.get("answerSeven"));
		answerSheet.setAnswerEight(answerMap.get("answerEight"));
		answerSheet.setAnswerNine(answerMap.get("answerNine"));
		answerSheet.setAnswerTen(answerMap.get("answerTen"));
		answerSheet.setUsers(users);
		users.setAnswerSheet(answerSheet);
		answerSheetRepository.save(answerSheet);
		return usersRepository.save(users);
	}

	@Override
	public AnswerKey createAnswerKey(AnswerKey answerkey) {
		return answerKeyRepository.save(answerkey);
	}

	@Override
	public String displayResult(Long id) {
		AnswerSheet answerSheet = answerSheetRepository.updateMarks(id);
		Long aId = (long) 1;
		AnswerKey answerKey = answerKeyRepository.findById(aId).orElse(null);
		int count = 0;
		if(answerSheet.getAnswerOne().equals(answerKey.getAnswerKeyOne())){count++;}
		if(answerSheet.getAnswerTwo().equals(answerKey.getAnswerKeyTwo())){count++;}
		if(answerSheet.getAnswerThree().equals(answerKey.getAnswerKeyThree())){count++;}
		if(answerSheet.getAnswerFour().equals(answerKey.getAnswerKeyFour())){count++;}
		if(answerSheet.getAnswerFive().equals(answerKey.getAnswerKeyFive())){count++;}
		if(answerSheet.getAnswerSix().equals(answerKey.getAnswerKeySix())){count++;}
		if(answerSheet.getAnswerSeven().equals(answerKey.getAnswerKeySeven())){count++;}
		if(answerSheet.getAnswerEight().equals(answerKey.getAnswerKeyEight())){count++;}
		if(answerSheet.getAnswerNine().equals(answerKey.getAnswerKeyNine())){count++;}
		if(answerSheet.getAnswerTen().equals(answerKey.getAnswerKeyTen())){count++;}
		if(count > 5)
		{
			return "Marks = " + count + " You are Pass";
		}
		return "Marks = " + count + " You are Fail";
	}
	
	@Override
	public List<QuestionsDto> showQuestions(){
		return questionsRepository.findAll().stream().map(this::convertingQuestionsDto).collect(Collectors.toList());
	}

	private QuestionsDto convertingQuestionsDto(Questions questions) {
		QuestionsDto questionsDto = new QuestionsDto();
		questionsDto.setQuestionNumber(questions.getQuestionId());
		questionsDto.setQuestion(questions.getQuestion());
		questionsDto.setOptionA(questions.getOptions().getOptionA());
		questionsDto.setOptionB(questions.getOptions().getOptionB());
		questionsDto.setOptionC(questions.getOptions().getOptionC());
		questionsDto.setOptionD(questions.getOptions().getOptionD());
		return questionsDto;
	}
	
	
	
	
}
