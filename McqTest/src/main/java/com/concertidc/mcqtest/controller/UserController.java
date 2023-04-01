package com.concertidc.mcqtest.controller;

import java.security.Principal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.concertidc.mcqtest.dto.UserRequest;
import com.concertidc.mcqtest.dto.UserResponse;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.service.IUserDetailService;

@Controller
@RequestMapping("/user")
public class UserController {
		
		@Autowired
		private IUserDetailService userService;
		@Autowired
		private AuthenticationManager authenticationManager;
	
		@PostMapping("/saveUser")
		public ResponseEntity<String> saveUser(@RequestBody Users user) {
			
			Long id = userService.saveUser(user);
			String message= "User with id '"+id+"' saved succssfully!";
			return ResponseEntity.ok(message);
		}
		
		@PostMapping("/loginUser")
		public ResponseEntity<?> login(@RequestBody UserRequest request,Principal p){
			try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					request.getUsername(), request.getPassword()));
			UserResponse result =  userService.showUserResponse(request.getUsername());
			return ResponseEntity.ok(result);
			}catch(BadCredentialsException e){return ResponseEntity.ok("Username or Password Mismatch");}	
		}
		
		@PostMapping("/getData")
		public ResponseEntity<String> testAfterLogin(Principal p){
			return ResponseEntity.ok("You are accessing data after a valid Login. You are :" +p.getName());
			}
		}
		
