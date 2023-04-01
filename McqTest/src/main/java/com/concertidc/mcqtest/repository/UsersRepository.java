package com.concertidc.mcqtest.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.concertidc.mcqtest.model.Users;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
	
	Optional<Users> findByUsername(String username);
	
	@Query(value = "Select user_id from users where username = ?1", nativeQuery = true)
	Long getUserIdByUsername(String username);
}
