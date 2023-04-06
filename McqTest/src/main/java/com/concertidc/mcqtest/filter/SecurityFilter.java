package com.concertidc.mcqtest.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.concertidc.mcqtest.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String requestURL = request.getRequestURL().toString();
			if (requestURL.contains("refreshtoken")) {
				String isRefreshToken = request.getHeader("isRefreshToken");
				String username = jwtUtils.getSubject(isRefreshToken);
				boolean isValidType = jwtUtils.getTokenType(isRefreshToken).contains("RefreshToken");
				if (isRefreshToken != null && isValidType) {
					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						UserDetails user = userDetailsService.loadUserByUsername(username);
						boolean isValid = jwtUtils.isValidToken(isRefreshToken, user.getUsername());
						if (isValid) {
							UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
									username, user.getPassword(), user.getAuthorities());
							authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(authToken);
						}
					}
				} else {
					throw new AuthenticationException("Token Type Mismatch: Not a refresh Token");
				}
			} else {
				String token = request.getHeader("Authorization");
				if (token != null) {
					String username = jwtUtils.getSubject(token);
					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						UserDetails user = userDetailsService.loadUserByUsername(username);
						boolean isValid = jwtUtils.isValidToken(token, user.getUsername());
						if (isValid) {
							UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
									username, user.getPassword(), user.getAuthorities());
							authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(authToken);
						}
					}
				}
			}
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException | AuthenticationException | SignatureException | MalformedJwtException exception) {
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			final Map<String, Object> body = new HashMap<>();
			body.put("status", HttpServletResponse.SC_NOT_ACCEPTABLE);
			body.put("error", "Unauthorized");
			body.put("message", exception.getMessage());
			body.put("path", request.getServletPath());
			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);
		}
	}

}
