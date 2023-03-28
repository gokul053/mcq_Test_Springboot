package com.concertidc.mcqtest.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
	
	Optional<Users> findByUserName(String userName);

	Boolean existsByUserName(String userName);

	Boolean existsByEmail(String email);
}
