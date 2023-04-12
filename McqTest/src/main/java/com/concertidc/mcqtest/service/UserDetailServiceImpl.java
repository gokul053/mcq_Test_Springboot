package com.concertidc.mcqtest.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.concertidc.mcqtest.advice.InputFormatErrorException;
import com.concertidc.mcqtest.config.AuthConstantStore;
import com.concertidc.mcqtest.config.ServiceConstantStore;
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.dto.RefreshTokenResponse;
import com.concertidc.mcqtest.dto.ResponseMessage;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.DepartmentRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.utils.JwtUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptEncoder;

	@Autowired
	private JwtUtils jwtUtils;

	//  Overidden Method for Authentication Purposes
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final Optional<Users> optionalUsers = this.usersRepository.findByUsername(username);

		org.springframework.security.core.userdetails.User userAuth = null;

		if (optionalUsers.isEmpty()) {
			throw new UsernameNotFoundException(ServiceConstantStore.USER_NOT_FOUND);
		} else {
			final Users user = optionalUsers.get();
			final Set<String> roles = user.getRoles();
			final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
			for (String role : roles) {
				grantedAuthorities.add(new SimpleGrantedAuthority(role));
			}

			userAuth = new org.springframework.security.core.userdetails.User(username, user.getPassword(),
					grantedAuthorities);
		}

		return userAuth;
	}

	//  Save Users to the Database
	public ResponseEntity<?> saveUser(Users user) {
		if (this.departmentRepository.findByDepartmentCode(user.getDepartment().getDepartmentCode()).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseMessage(ServiceConstantStore.INVALID_DEPT_CODE));
		}
		user.setPassword(bCryptEncoder.encode(user.getPassword()));
		this.usersRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(ServiceConstantStore.USER_SAVED));
	}

	public Optional<Users> findByUsername(String username) {
		return this.usersRepository.findByUsername(username);
	}

	//  Login Code
	public LoginResponse login(String username) {
		final String accessToken = this.jwtUtils.generateToken(username);
		final String refreshToken = this.jwtUtils.generateRefreshToken(username);
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException(ServiceConstantStore.USER_NOT_FOUND));
		final LoginResponse userResponse = new LoginResponse();
		userResponse.setAddress(users.getAddress());
		userResponse.setDepartment(users.getDepartment().getDepartmentName());
		userResponse.setFirstName(users.getFirstName());
		userResponse.setLastName(users.getLastName());
		userResponse.setUsername(username);
		userResponse.setUserId(users.getUserId());
		userResponse.setAccessToken(accessToken);
		userResponse.setRefreshToken(refreshToken);
		return userResponse;
	}

	//  Refresh Token Code
	public RefreshTokenResponse generateNewAccessToken(HttpServletRequest request) {
		final RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
		final String refreshToken = request.getHeader(AuthConstantStore.HEADER_STRING_REFRESH);
		final String username = this.jwtUtils.getSubject(refreshToken);
		final String token = this.jwtUtils.generateRefreshToken(username);
		refreshTokenResponse.setUsername(username);
		refreshTokenResponse.setNewAccessToken(token);
		return refreshTokenResponse;
	}

	//  Validating Credentials for Login Process
	public void validateCredentials(String username, String password)
			throws AuthenticationException, NullPointerException {
		if (username == null || password == null) {
			throw new InputFormatErrorException();
		}
		if (findByUsername(username).isEmpty()) {
			throw new UsernameNotFoundException(ServiceConstantStore.USER_NOT_FOUND);
		}
	}

}
