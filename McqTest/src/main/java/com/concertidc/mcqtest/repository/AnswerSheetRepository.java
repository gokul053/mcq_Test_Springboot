package com.concertidc.mcqtest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.model.AnswerSheet;
import com.concertidc.mcqtest.model.Users;

@Repository
public interface AnswerSheetRepository extends JpaRepository<AnswerSheet, Long> {

	List<AnswerSheet> findByUsers(Users users);
	
}
