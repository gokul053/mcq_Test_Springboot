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

import com.concertidc.mcqtest.dto.RefreshTokenResponse;
import com.concertidc.mcqtest.dto.ResponseMessage;
import com.concertidc.mcqtest.advice.InputFormatErrorException;
import com.concertidc.mcqtest.dto.LoginResponse;
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final Optional<Users> optionalUsers = usersRepository.findByUsername(username);

		org.springframework.security.core.userdetails.User userAuth = null;

		if (optionalUsers.isEmpty()) {
			throw new UsernameNotFoundException("User with username: " + username + " not found");
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

	public ResponseEntity<?> saveUser(Users user) {
		if (this.departmentRepository.findByDepartmentCode(user.getDepartment().getDepartmentCode()).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Department Code Invalid"));
		}
		user.setPassword(bCryptEncoder.encode(user.getPassword()));
		final Long id = this.usersRepository.save(user).getUserId();
		return ResponseEntity.ok(new ResponseMessage("User Saved with the ID : " + id + " in the Database"));
	}
	
	public ResponseEntity<?> editUser(HttpServletRequest request,Users editUser) {
		final String username = this.jwtUtils.getSubject(request.getHeader("Authorization"));
		final Users user = this.usersRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));
		
		user.setFirstName(editUser.getFirstName());
		user.setLastName(editUser.getLastName());
		user.setEmail(editUser.getEmail());
		this.usersRepository.save(user);
		return ResponseEntity.ok(new ResponseMessage("User Details Changed SuccessFully"));
	}

	public Optional<Users> findByUsername(String username) {
		return this.usersRepository.findByUsername(username);
	}

	public LoginResponse login(String username) {
		final String accessToken = this.jwtUtils.generateToken(username);
		final String refreshToken = this.jwtUtils.generateRefreshToken(username);
		final Users users = this.usersRepository.findByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		LoginResponse userResponse = new LoginResponse();
		userResponse.setAddress(users.getAddress());
		userResponse.setDepartment(users.getDepartment().getDepartmentName());
		userResponse.setEmail(users.getEmail());
		userResponse.setFirstName(users.getFirstName());
		userResponse.setLastName(users.getLastName());
		userResponse.setUsername(username);
		userResponse.setUserId(users.getUserId());
		userResponse.setAccessToken(accessToken);
		userResponse.setRefreshToken(refreshToken);
		return userResponse;
	}

	public RefreshTokenResponse generateNewAccessToken(HttpServletRequest request) {
		RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
		final String refreshToken = request.getHeader("isRefreshToken");
		final String username = this.jwtUtils.getSubject(refreshToken);
		final String token = this.jwtUtils.generateRefreshToken(username);
		refreshTokenResponse.setUsername(username);
		refreshTokenResponse.setNewAccessToken(token);
		return refreshTokenResponse;
	}

	public void validateCredentials(String username, String password)
			throws AuthenticationException, NullPointerException {
		if (username == null || password == null ) {
			throw new InputFormatErrorException();
		}
		if (findByUsername(username).isEmpty()) {
			throw new UsernameNotFoundException("Username Not Found");
		}
	}

}
