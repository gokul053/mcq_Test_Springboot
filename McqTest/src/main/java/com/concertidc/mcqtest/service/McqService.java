package com.concertidc.mcqtest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.concertidc.mcqtest.advice.QuestionNotFoundException;
import com.concertidc.mcqtest.config.AuthConstantStore;
import com.concertidc.mcqtest.config.ServiceConstantStore;
import com.concertidc.mcqtest.dto.MarksFilter;
import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.dto.ResponseMessage;
import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.UserAnswers;
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

import jakarta.persistence.EntityExistsException;
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

	// Creating Questions and Saving in DB
	public ResponseEntity<?> createQuestions(List<Questions> questions) {
		this.questionsRepository.deleteAll();
		this.questionsRepository.saveAll(questions);
		return ResponseEntity.ok(new ResponseMessage(ServiceConstantStore.QUESTIONS_SAVED));
	}

	// Write Exam By Users
	public ResponseEntity<?> writeExam(HttpServletRequest request, List<UserAnswers> userAnswers) {
		final String username = this.jwtUtils.getSubject(request.getHeader(AuthConstantStore.HEADER_STRING));
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(ServiceConstantStore.QUESTION_NOT_FOUND));
		first: for (UserAnswers userAnswer : userAnswers) {
			final List<UserAnswers> checkAnswerList = answerSheetRepository.findByUsers(users);
			for (UserAnswers checkAnswers : checkAnswerList) {
				if (checkAnswers.getQuestions().getQuestionId().equals(userAnswer.getQuestions().getQuestionId())) {
					continue first;
				}
			}
			final Questions questions = this.questionsRepository.findById(userAnswer.getQuestions().getQuestionId())
					.orElseThrow(() -> new QuestionNotFoundException());
			userAnswer.setUsers(users);
			userAnswer.setQuestions(questions);
			userAnswer.setAnswer(userAnswer.getAnswer());
			this.answerSheetRepository.save(userAnswer);
		}
		return ResponseEntity.ok(new ResponseMessage(ServiceConstantStore.ANSWER_SAVED));
	}

	// Display User Result
	public String displayResult(HttpServletRequest request) throws Exception {
		final String username = this.jwtUtils.getSubject(request.getHeader(AuthConstantStore.HEADER_STRING));
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException(ServiceConstantStore.USER_NOT_FOUND));
		final List<UserAnswers> answerSheets = this.answerSheetRepository.findByUsers(users);
		final List<Questions> questions = this.questionsRepository.findAll();
		final List<String> answer = new ArrayList<>();
		final List<String> answerKey = new ArrayList<>();
		int count = 0;
		for (UserAnswers answerSheet : answerSheets) {
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

	// Display All Questions
	public List<QuestionsDto> displayQuestions() {
		return this.questionsRepository.findAll().stream().map(this::convertingQuestionsDto)
				.collect(Collectors.toList());
	}

	private QuestionsDto convertingQuestionsDto(Questions questions) {
		final QuestionsDto questionsDto = new QuestionsDto();
		questionsDto.setQuestionNumber(questions.getQuestionId());
		questionsDto.setQuestion(questions.getQuestion());
		questionsDto.setA(questions.getOptions().getA());
		questionsDto.setB(questions.getOptions().getB());
		questionsDto.setC(questions.getOptions().getC());
		questionsDto.setD(questions.getOptions().getD());
		return questionsDto;
	}

	// Calculate Marks List
	public List<MarksFilter> calculateMarks() {
		final List<Users> users = this.usersRepository.findAll();
		final List<UsersDto> passUserList = new ArrayList<>();
		final List<UsersDto> failUserList = new ArrayList<>();
		final List<MarksFilter> marksList = new ArrayList<>();
		for (Users user : users) {
			if (user.getRoles().contains(AuthConstantStore.ROLE_USER)) {
				int count = 0;
				int totalMarks = 0;
				final List<UserAnswers> answerSheets = this.answerSheetRepository.findByUsers(user);
				final List<Questions> questions = this.questionsRepository.findAll();
				final List<String> answer = new ArrayList<>();
				final List<String> answerKey = new ArrayList<>();
				for (UserAnswers answerSheet : answerSheets) {
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
				final UsersDto usersDto = new UsersDto();
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
		final MarksFilter marksFilter = new MarksFilter();
		marksFilter.setPassedCandidates(passUserList);
		marksFilter.setFailedCandidates(failUserList);
		marksList.add(marksFilter);
		return marksList;
	}

	// Update Department Table
	public String updateDepartmentList(Department department) {
		final List<Department> departmentList = this.departmentRepository
				.findByDepartmentCode(department.getDepartmentCode());
		if (departmentList.isEmpty()) {
			throw new EntityExistsException("Department already Exist in the Database");
		}
		final String code = this.departmentRepository.save(department).getDepartmentCode();
		return code;
	}

}
