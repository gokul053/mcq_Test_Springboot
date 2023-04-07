package com.concertidc.mcqtest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.concertidc.mcqtest.advice.QuestionNotFoundException;
import com.concertidc.mcqtest.dto.MarksFilter;
import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.dto.ResponseMessage;
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
import com.concertidc.mcqtest.utils.JwtUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class McqService {

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

	@Autowired
	JwtUtils jwtUtils;

	public String createUsers(Users users) {
		this.usersRepository.save(users);
		return "Account Created, User Id = " + users.getUserId();
	}

	public ResponseEntity<?> createQuestions(Questions questions) {
		if (questions.getQuestionId() > 10) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Max Question Limit 10"));
		}
		this.questionsRepository.save(questions);
		return ResponseEntity.ok(new ResponseMessage("Questions Saved"));
	}

	public ResponseEntity<?> writeExam(HttpServletRequest request, Long questionNumber, AnswerSheet answerSheet) {
		final String username = this.jwtUtils.getSubject(request.getHeader("Authorization"));
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
		final Questions questions = this.questionsRepository.findById(questionNumber)
				.orElseThrow(() -> new QuestionNotFoundException());
		final List<AnswerSheet> answerSheetList = answerSheetRepository.findByUsers(users);
		if (answerSheet.getAnswer() == null || answerSheet.getAnswer().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseMessage("Provide Answer in correct Format"));
		}
		for (AnswerSheet answerSheets : answerSheetList) {
			if (answerSheets.getQuestions().getQuestionId().equals(questionNumber)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ResponseMessage("Answer Already Saved For this Question"));
			}
		}
		answerSheet.setUsers(users);
		answerSheet.setQuestions(questions);
		answerSheet.setAnswer(answerSheet.getAnswer());
		this.answerSheetRepository.save(answerSheet);
		return ResponseEntity.ok(new ResponseMessage("Answer Saved to the Answersheet"));
	}

	public String displayResult(HttpServletRequest request) throws Exception {
		final String username = this.jwtUtils.getSubject(request.getHeader("Authorization"));
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		final List<AnswerSheet> answerSheets = answerSheetRepository.findByUsers(users);
		final List<Questions> questions = questionsRepository.findAll();
		List<String> answer = new ArrayList<>();
		List<String> answerKey = new ArrayList<>();
		int count = 0;
		for (AnswerSheet answerSheet : answerSheets) {
			answer.add(answerSheet.getAnswer());
		}
		for (Questions question : questions) {
			answerKey.add(question.getAnswerKey().getAnswerKey());
		}
		for (int i = 0; i < answer.size(); i++) {
			if (answer.get(i) != null && answerKey.get(i) != null) {
				if (answer.get(i).equals(answerKey.get(i))) {
					count++;
				}
			}
		}
		if (count < 5)
			return "Marks : " + count + ", You're Fail";
		else
			return "Marks : " + count + ", You're Pass";
	}

	public Optional<AnswerKey> getAnswerKey(Long id) {
		return this.answerKeyRepository.findById(id);
	}

	public List<QuestionsDto> displayQuestions() {
		return this.questionsRepository.findAll().stream().map(this::convertingQuestionsDto).collect(Collectors.toList());
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

	public List<MarksFilter> calculateMarks() {
		final List<Users> users = usersRepository.findAll();
		final List<UsersDto> passUserList = new ArrayList<>();
		final List<UsersDto> failUserList = new ArrayList<>();
		final List<MarksFilter> marksList = new ArrayList<>();
		for (Users user : users) {
			if (user.getRoles().contains("User")) {
				int count = 0;
				int totalMarks = 0;
				final List<AnswerSheet> answerSheets = this.answerSheetRepository.findByUsers(user);
				final List<Questions> questions = this.questionsRepository.findAll();
				final List<String> answer = new ArrayList<>();
				final List<String> answerKey = new ArrayList<>();
				for (AnswerSheet answerSheet : answerSheets) {
					answer.add(answerSheet.getAnswer());
				}
				for (Questions question : questions) {
					answerKey.add(question.getAnswerKey().getAnswerKey());
					totalMarks++;
				}
				for (int i = 0; i < answer.size(); i++) {
					if (answer.get(i).equals(answerKey.get(i))) {
						count++;
					}
				}
				UsersDto usersDto = new UsersDto();
				usersDto.setUserId(user.getUserId());
				usersDto.setFirstName(user.getFirstName());
				usersDto.setLastName(user.getLastName());
				usersDto.setTotalMarks(totalMarks);
				usersDto.setObtainedMarks(count);
				if (count > 5) {
					passUserList.add(usersDto);
				} else {
					failUserList.add(usersDto);
				}
			}
		}
		MarksFilter marksFilter = new MarksFilter();
		marksFilter.setPassedCandidates(passUserList);
		marksFilter.setFailedCandidates(failUserList);
		marksList.add(marksFilter);
		return marksList;
	}
	
	public String updateDepartmentList(Department department) {
		final String code = this.departmentRepository.save(department).getDepartmentCode();
		return code;
	}
	
}
