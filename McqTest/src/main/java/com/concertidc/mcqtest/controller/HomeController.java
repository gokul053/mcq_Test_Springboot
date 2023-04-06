package com.concertidc.mcqtest.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concertidc.mcqtest.dto.RefreshTokenResponse;
import com.concertidc.mcqtest.dto.LoginRequest;
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;

@RestController
@RequestMapping("/home")
public class HomeController {

	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/login-user")
	public ResponseEntity<?> login(@RequestBody LoginRequest request)
			throws AuthenticationException, NullPointerException {
		userDetailServiceImpl.validateCredentials(request.getUsername(), request.getPassword());
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		LoginResponse loginResponse = userDetailServiceImpl.login(request.getUsername());
		return ResponseEntity.ok(loginResponse);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(Principal principal) {
		RefreshTokenResponse result = userDetailServiceImpl.generateNewAccessToken(principal);
		return ResponseEntity.ok(result);
	}
}
