package com.concertidc.mcqtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.concertidc.mcqtest.entity.AnswerKey;

public interface AnswerKeyRepository extends JpaRepository<AnswerKey, Long> {

}
