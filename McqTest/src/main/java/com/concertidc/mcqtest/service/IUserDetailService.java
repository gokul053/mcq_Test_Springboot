package com.concertidc.mcqtest.service;

import java.util.Optional;

import com.concertidc.mcqtest.dto.UserResponse;
import com.concertidc.mcqtest.model.Users;

public interface IUserDetailService {

	Long saveUser(Users user);
	
	Optional<Users> findByUsername(String username);

	UserResponse showUserResponse(String string);
	
}
