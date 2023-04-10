package com.concertidc.mcqtest.advice;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.concertidc.mcqtest.config.ErrorMessageStore;
import com.concertidc.mcqtest.dto.ResponseMessage;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ErrorhandlerControllerAdvice {

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<?> SqlErrorHandler() {
		final String message = ErrorMessageStore.USER_EXISTS;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<?> usernameErrorHandler() {
		final String message = ErrorMessageStore.USER_NOT_FOUND;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> badCredsException() {
		final String message = ErrorMessageStore.PASSWORD_ERROR;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(InputFormatErrorException.class)
	public ResponseEntity<?> inputFormatError() {
		final String message = ErrorMessageStore.FORMAT_INVALID;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(QuestionNotFoundException.class)
	public ResponseEntity<?> questionNotFound()
	{
		final String message = ErrorMessageStore.Q_NO_INVALID;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> constraintNullHandler() {
		final String message = ErrorMessageStore.FIELD_IS_NULL;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
	}
	
	@ExceptionHandler(EntityExistsException.class)
	public ResponseEntity<?> entityExistsHandler()
	{
		final String message = ErrorMessageStore.ENTITY_EXISTS;
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));	
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> exceptionHandler()
	{
		final String message = ErrorMessageStore.ERROR_OCCURED;
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage(message));
	}
}
