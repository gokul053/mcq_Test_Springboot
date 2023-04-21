package com.concertidc.mcqtest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.concertidc.mcqtest.advice.DataNotFoundException;
import com.concertidc.mcqtest.advice.DuplicateAnswerException;
import com.concertidc.mcqtest.advice.QuestionNotFoundException;
import com.concertidc.mcqtest.config.AuthConstantStore;
import com.concertidc.mcqtest.config.ServiceConstantStore;
import com.concertidc.mcqtest.dto.AllUserDto;
import com.concertidc.mcqtest.dto.MarksFilter;
import com.concertidc.mcqtest.dto.QuestionsDto;
import com.concertidc.mcqtest.dto.ResponseMessage;
import com.concertidc.mcqtest.dto.UsersDto;
import com.concertidc.mcqtest.model.Department;
import com.concertidc.mcqtest.model.Questions;
import com.concertidc.mcqtest.model.UserAnswers;
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
	@Transactional
	public ResponseEntity<ResponseMessage> createQuestions(List<Questions> questions) {
		if (questionsRepository.findAll().isEmpty()) {
			this.questionsRepository.saveAll(questions);
			return ResponseEntity.ok(new ResponseMessage(ServiceConstantStore.QUESTIONS_SAVED));
		} else {
			return ResponseEntity.ok(new ResponseMessage(ServiceConstantStore.POP_UP_RESET));
		}
	}

	// Write Exam By Users
	@Transactional
	public ResponseEntity<ResponseMessage> writeExam(HttpServletRequest request, List<UserAnswers> userAnswers) {
		final String username = this.jwtUtils.getSubject(request.getHeader(AuthConstantStore.HEADER_STRING));
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(ServiceConstantStore.QUESTION_NOT_FOUND));
		if (this.answerSheetRepository.findByUsers(users).isEmpty()) {
			userAnswers.forEach(userAnswer -> {
				final Questions questions = this.questionsRepository.findById(userAnswer.getQuestions().getQuestionId())
						.orElseThrow(() -> new QuestionNotFoundException());
				userAnswer.setUsers(users);
				userAnswer.setQuestions(questions);
				userAnswer.setAnswer(userAnswer.getAnswer());
				this.answerSheetRepository.save(userAnswer);
			});
		} else {
			throw new DuplicateAnswerException();
		}
		return ResponseEntity.ok(new ResponseMessage(ServiceConstantStore.ANSWER_SAVED));
	}

	// Display User Result
	public String displayResult(HttpServletRequest request) throws Exception {
		final String username = this.jwtUtils.getSubject(request.getHeader(AuthConstantStore.HEADER_STRING));
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException(ServiceConstantStore.USER_NOT_FOUND));
		final List<UserAnswers> userAnswers = this.answerSheetRepository.findByUsers(users);
		final List<Questions> questions = this.questionsRepository.findAll();
		final List<String> answer = new ArrayList<>();
		final List<String> answerKey = new ArrayList<>();
		int count = 0;
		userAnswers.forEach(userAnswer -> answer.add(userAnswer.getAnswer()));
		questions.forEach(question -> answerKey.add(question.getAnswerKey().getAnswerKey()));
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
		if(this.questionsRepository.findAll().isEmpty()) {
			throw new DataNotFoundException();
		}
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
		users.forEach(user ->  {
			if (user.getRoles().contains(AuthConstantStore.ROLE_USER)) {
				int count = 0;
				final List<UserAnswers> userAnswers = this.answerSheetRepository.findByUsers(user);
				if (userAnswers.isEmpty()) {
					return;
				}
				final List<Questions> questions = this.questionsRepository.findAll();
				final List<String> answer = new ArrayList<>();
				final List<String> answerKey = new ArrayList<>();
				userAnswers.forEach(userAnswer -> answer.add(userAnswer.getAnswer()));
				questions.forEach(question -> answerKey.add(question.getAnswerKey().getAnswerKey()));
				int totalMarks = answerKey.size();
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
				if (count >= 5) {
					passUserList.add(usersDto);
				} else {
					failUserList.add(usersDto);
				}
			}
		});
		final MarksFilter marksFilter = new MarksFilter();
		marksFilter.setPassedCandidates(passUserList);
		marksFilter.setFailedCandidates(failUserList);
		marksList.add(marksFilter);
		return marksList;
	}

	// Update Department Table
	@Transactional
	public String updateDepartmentList(Department department) {
		final List<Department> departmentList = this.departmentRepository
				.findByDepartmentCode(department.getDepartmentCode());
		if (departmentList.isEmpty() == false) {
			throw new EntityExistsException(ServiceConstantStore.DEPARTMENT_EXISTS);
		}
		final String code = this.departmentRepository.save(department).getDepartmentCode();
		return code;
	}

	// Reintializing Test
	public ResponseEntity<?> reintializeTest() {
		this.answerSheetRepository.deleteAll();
		this.questionsRepository.deleteAll();
		return ResponseEntity.ok(new ResponseMessage("All data's are Deleted"));
	}

	// All User List
	public List<AllUserDto> listAllUsers() {
		return this.usersRepository.findAll().stream().map(this::convertingUsersDto)
				.collect(Collectors.toList());
	}
	
	public AllUserDto convertingUsersDto(Users users)
	{
		AllUserDto allUserDto = new AllUserDto();
		allUserDto.setUsername(users.getUsername());
		allUserDto.setFirstName(users.getFirstName());
		allUserDto.setLastName(users.getLastName());
		allUserDto.setDepartment(users.getDepartment().getDepartmentName());
		allUserDto.setAddress(users.getAddress());
		return allUserDto;
	}
	

}
