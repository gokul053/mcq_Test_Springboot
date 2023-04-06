package com.concertidc.mcqtest.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.concertidc.mcqtest.dto.ResponseMessage;
import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.AnswerKey;
import com.concertidc.mcqtest.model.AnswerSheet;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.AnswerKeyRepository;
import com.concertidc.mcqtest.repository.AnswerSheetRepository;
import com.concertidc.mcqtest.repository.DepartmentRepository;
import com.concertidc.mcqtest.repository.OptionsRepository;
import com.concertidc.mcqtest.repository.QuestionsRepository;
import com.concertidc.mcqtest.repository.UsersRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class McqServiceImpl {

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

	@Autowired
	DepartmentRepository departmentRepository;

	public String createUsers(Users users) {
		usersRepository.save(users);
		return "Account Created, User Id = " + users.getUserId();
	}

	public List<Users> displayAllUsers() {
		return usersRepository.findAll();
	}

	public ResponseEntity<?> createQuestions(Questions questions) {
		if (questions.getQuestionId() > 10) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Max Question Limit 10"));
		}
		questionsRepository.save(questions);
		return ResponseEntity.ok(new ResponseMessage("Questions Saved"));
	}

	public List<Questions> displayAllQuestions() {
		return questionsRepository.findAll();
	}

	public ResponseEntity<?> writeExam(Principal principal, Long questionNumber, Map<String, String> answerMap) {
		String username = principal.getName();
		Users users = usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		Questions questions = questionsRepository.findById(questionNumber)
				.orElseThrow(() -> new EntityNotFoundException("Question not found"));
		List<AnswerSheet> answerSheetList = answerSheetRepository.findByUsers(users);
		for (AnswerSheet answerSheet : answerSheetList) {
			if (answerSheet.getQuestions().getQuestionId().equals(questionNumber)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseMessage("Answer Already Saved For this Question"));
			}
		}
		AnswerSheet answerSheet = new AnswerSheet();
		answerSheet.setUsers(users);
		answerSheet.setQuestions(questions);
		answerSheet.setAnswer(answerMap.get("answer"));
		answerSheetRepository.save(answerSheet);
		return ResponseEntity.ok("Answer Saved to the Answersheet");
	}

	public AnswerKey createAnswerKey(AnswerKey answerkey) {
		return answerKeyRepository.save(answerkey);
	}

	public String displayResult(Principal principal) throws Exception {
		String username = principal.getName();
		Users users = usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		List<AnswerSheet> answerSheets = answerSheetRepository.findByUsers(users);
		List<Questions> questions = questionsRepository.findAll();
		List<String> answer = new ArrayList<>();
		List<String> answerKey = new ArrayList<>();
		int count = 0;
		for (AnswerSheet answerSheet : answerSheets) {
			answer.add(answerSheet.getAnswer());
		}
		for (Questions question : questions) {
			answerKey.add(question.getAnswerKey().getAnswerKey());
		}
		for (int i = 0; i < 10; i++) {
			if (answer.get(i).equals(answerKey.get(i))) {
				count++;
			}
		}
		if (count < 5)
			return "Marks : " + count + ", You're Fail";
		else
			return "Marks : " + count + ", You're Pass";
	}

	public Optional<AnswerKey> getAnswerKey(Long id) {
		return answerKeyRepository.findById(id);
	}

	public List<QuestionsDto> displayQuestions() {
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

	public List<UsersDto> filterMarksAboveSeven() {
		int count = 0;
		List<Users> users = usersRepository.findAll();
		List<UsersDto> userList = new ArrayList<>();
		for (Users user : users) {
			if (user.getRoles().contains("User")) {
				List<AnswerSheet> answerSheets = answerSheetRepository.findByUsers(user);
				List<Questions> questions = questionsRepository.findAll();
				List<String> answer = new ArrayList<>();
				List<String> answerKey = new ArrayList<>();
				for (AnswerSheet answerSheet : answerSheets) {
					answer.add(answerSheet.getAnswer());
				}
				for (Questions question : questions) {
					answerKey.add(question.getAnswerKey().getAnswerKey());
				}
				for (int i = 0; i < 10; i++) {
					if (answer.get(i).equals(answerKey.get(i))) {
						count++;
						System.out.println(answer.get(i));
					}
				}
				if (count >= 7) {
					UsersDto usersDto = new UsersDto();
					usersDto.setUserId(user.getUserId());
					usersDto.setFirstName(user.getFirstName());
					usersDto.setLastName(user.getLastName());
					userList.add(usersDto);
				}
			}
		}
		return userList;
	}

	public List<UsersDto> filterMarksBelowSeven() {
		int count = 0;
		List<Users> users = usersRepository.findAll();
		List<UsersDto> userList = new ArrayList<>();
		for (Users user : users) {
			if (user.getRoles().contains("User")) {
				List<AnswerSheet> answerSheets = answerSheetRepository.findByUsers(user);
				List<Questions> questions = questionsRepository.findAll();
				List<String> answer = new ArrayList<>();
				List<String> answerKey = new ArrayList<>();
				for (AnswerSheet answerSheet : answerSheets) {
					answer.add(answerSheet.getAnswer());
				}
				for (Questions question : questions) {
					answerKey.add(question.getAnswerKey().getAnswerKey());
				}
				for (int i = 0; i < 10; i++) {
					if (answer.get(i).equals(answerKey.get(i))) {
						count++;
					}
				}
				if (count < 7) {
					UsersDto usersDto = new UsersDto();
					usersDto.setUserId(user.getUserId());
					usersDto.setFirstName(user.getFirstName());
					usersDto.setLastName(user.getLastName());
					userList.add(usersDto);
				}
			}
		}
		return userList;
	}

	public String updateDepartmentList(Department department) {
		String code = departmentRepository.save(department).getDepartmentCode();
		return code;
	}

}
