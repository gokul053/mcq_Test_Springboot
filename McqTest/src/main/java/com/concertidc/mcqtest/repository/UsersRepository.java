package com.concertidc.mcqtest.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.entity.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
	
}
