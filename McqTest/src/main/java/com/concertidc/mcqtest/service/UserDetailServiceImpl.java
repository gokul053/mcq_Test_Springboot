package com.concertidc.mcqtest.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.concertidc.mcqtest.dto.UserResponse;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.utils.JwtUtils;

import jakarta.persistence.EntityNotFoundException;


@Service
public class UserDetailServiceImpl implements IUserDetailService,UserDetailsService {

	@Autowired
	private UsersRepository userRepo; 
	
	@Autowired
	private BCryptPasswordEncoder bCryptEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Override
	public Long saveUser(Users user) {
		
		//Encode password before saving to DB
		user.setPassword(bCryptEncoder.encode(user.getPassword()));
		return userRepo.save(user).getUserId();
	}

	//find user by username
	@Override
	public Optional<Users> findByUsername(String username) {
		return userRepo.findByUsername(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> opt = userRepo.findByUsername(username);
		
		org.springframework.security.core.userdetails.User springUser=null;
		
		if(opt.isEmpty()) {
			throw new UsernameNotFoundException("User with username: " +username +" not found");
		}else {
			Users user =opt.get();	//retrieving user from DB
			Set<String> roles = user.getRoles();
			Set<GrantedAuthority> ga = new HashSet<>();
			for(String role:roles) {
				ga.add(new SimpleGrantedAuthority(role));
			}
			
			springUser = new org.springframework.security.core.userdetails.User(
							username,
							user.getPassword(),
							ga );
		}
		
		return springUser;
	}

	@Override
	public UserResponse showUserResponse(String username) {
		String token =jwtUtils.generateToken(username);
		Long id = userRepo.getUserIdByUsername(username);
		Users users = userRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
		UserResponse userResponse = new UserResponse();
		userResponse.setAddress(users.getAddress());
		userResponse.setDepartment(users.getDepartment());
		userResponse.setEmail(users.getEmail());
		userResponse.setName(users.getName());
		userResponse.setUsername(username);
		userResponse.setUserId(id);
		userResponse.setToken(token);
		return userResponse;
	}

}
	
