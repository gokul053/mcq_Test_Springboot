package com.concertidc.mcqtest.advice;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.concertidc.mcqtest.dto.ResponseMessage;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorhandlerControllerAdvice {

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<?> SqlErrorHandler() {
		String message = "Username or Email already exists";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<?> usernameErrorHandler() {
		String message = "Username not found in the Database!";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> badCredsException() {
		String message = "Password Error";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<?> nullPointerException() {
		String message = "Field Can't be Null";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> constraintNullHandler() {
		String message = "Field Can't be Null";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}
}
