package com.concertidc.mcqtest.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.model.AnswerSheet;

@Repository
public interface AnswerSheetRepository extends JpaRepository<AnswerSheet, Long> {
	
	@Query(value = "select * from Answer_Sheet where user_id = ?1", nativeQuery = true)
	public AnswerSheet updateMarks(Long id);
}
