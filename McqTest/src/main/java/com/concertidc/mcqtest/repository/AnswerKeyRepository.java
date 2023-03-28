package com.concertidc.mcqtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concertidc.mcqtest.model.AnswerKey;

public interface AnswerKeyRepository extends JpaRepository<AnswerKey, Long> {

}
