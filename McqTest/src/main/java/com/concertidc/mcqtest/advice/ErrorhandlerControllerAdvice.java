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
		final String message = "Username or Email already exists";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<?> usernameErrorHandler() {
		final String message = "Username not found in the Database!";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> badCredsException() {
		final String message = "Password Error";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(InputFormatErrorException.class)
	public ResponseEntity<?> inputFormatError() {
		final String message = "Input Request Format is Invalid";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(QuestionNotFoundException.class)
	public ResponseEntity<?> questionNotFound()
	{
		final String message = "Question Number is Invalid";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> constraintNullHandler() {
		final String message = "Field Can't be Null";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exceptionHandler()
	{
		final String message = "Error occured";
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message));
	}
}
