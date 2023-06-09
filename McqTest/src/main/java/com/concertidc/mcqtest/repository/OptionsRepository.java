package com.concertidc.mcqtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.model.Options;

@Repository
public interface OptionsRepository extends JpaRepository<Options, Long> {

}
