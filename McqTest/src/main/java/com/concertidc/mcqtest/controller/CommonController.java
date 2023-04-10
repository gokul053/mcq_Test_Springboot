package com.concertidc.mcqtest.controller;

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
import com.concertidc.mcqtest.config.EndPointStore;
import com.concertidc.mcqtest.dto.LoginRequest;
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.service.UserDetailServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(EndPointStore.COMMON_ENDPOINT)
public class CommonController {

	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping(EndPointStore.LOGIN_USER)
	public ResponseEntity<?> login(@RequestBody LoginRequest request)
			throws AuthenticationException, NullPointerException {
		this.userDetailServiceImpl.validateCredentials(request.getUsername(), request.getPassword());
		this.authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		final LoginResponse loginResponse = this.userDetailServiceImpl.login(request.getUsername());
		return ResponseEntity.ok(loginResponse);
	}

	@PostMapping(EndPointStore.REFRESH_TOKEN)
	public ResponseEntity<?> refreshToken(HttpServletRequest request) {
		final RefreshTokenResponse result = userDetailServiceImpl.generateNewAccessToken(request);
		return ResponseEntity.ok(result);
	}
}
