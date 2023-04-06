package com.concertidc.mcqtest.service;

import java.security.Principal;
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
import com.concertidc.mcqtest.dto.LoginResponse;
import com.concertidc.mcqtest.model.Users;
import com.concertidc.mcqtest.repository.DepartmentRepository;
import com.concertidc.mcqtest.repository.UsersRepository;
import com.concertidc.mcqtest.utils.JwtUtils;

import jakarta.persistence.EntityNotFoundException;

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
		Optional<Users> optionalUsers = usersRepository.findByUsername(username);

		org.springframework.security.core.userdetails.User userAuth = null;

		if (optionalUsers.isEmpty()) {
			throw new UsernameNotFoundException("User with username: " + username + " not found");
		} else {
			Users user = optionalUsers.get();
			Set<String> roles = user.getRoles();
			Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
			for (String role : roles) {
				grantedAuthorities.add(new SimpleGrantedAuthority(role));
			}

			userAuth = new org.springframework.security.core.userdetails.User(username, user.getPassword(),
					grantedAuthorities);
		}

		return userAuth;
	}

	public ResponseEntity<?> saveUser(Users user) {
		if (departmentRepository.findByDepartmentCode(user.getDepartment().getDepartmentCode()).isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Department Code Invalid"));
		}
		user.setPassword(bCryptEncoder.encode(user.getPassword()));
		Long id = usersRepository.save(user).getUserId();
		return ResponseEntity.ok("User Saved with the ID : " + id + " in the Database");
	}

	public Optional<Users> findByUsername(String username) {
		return usersRepository.findByUsername(username);
	}

	public LoginResponse login(String username) {
		String accessToken = jwtUtils.generateToken(username);
		String refreshToken = jwtUtils.generateRefreshToken(username);
		Users users = usersRepository.findByUsername(username)
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

	public RefreshTokenResponse generateNewAccessToken(Principal principal) {
		RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
		String username = principal.getName();
		String token = jwtUtils.generateRefreshToken(username);
		refreshTokenResponse.setUsername(username);
		refreshTokenResponse.setNewAccessToken(token);
		return refreshTokenResponse;
	}

	public void validateCredentials(String username, String password)
			throws AuthenticationException, NullPointerException {

		if (username.isBlank()) {
			throw new NullPointerException();
		}
		if (findByUsername(username).isEmpty()) {
			throw new UsernameNotFoundException("Username Not Found");
		}

	}

}
