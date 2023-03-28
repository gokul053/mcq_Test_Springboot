package com.concertidc.mcqtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concertidc.mcqtest.model.Questions;

public interface QuestionsRepository extends JpaRepository<Questions, Long>{

}
