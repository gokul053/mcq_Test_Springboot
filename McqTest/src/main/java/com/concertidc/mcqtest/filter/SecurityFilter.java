package com.concertidc.mcqtest.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.concertidc.mcqtest.config.AuthConstantStore;
import com.concertidc.mcqtest.config.ErrorMessageStore;
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
			if (requestURL.contains("refresh-token")) {
				final String isRefreshToken = request.getHeader(AuthConstantStore.HEADER_STRING_REFRESH);
				final String username = this.jwtUtils.getSubject(isRefreshToken);
				if (isRefreshToken != null) {
					if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						final UserDetails user = this.userDetailsService.loadUserByUsername(username);
						final boolean isValid = this.jwtUtils.isValidToken(isRefreshToken, user.getUsername());
						if (isValid) {
							UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
									username, user.getPassword(), user.getAuthorities());
							authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(authToken);
						}
					}
				}
			} else {
				final String token = request.getHeader(AuthConstantStore.HEADER_STRING);
				if (token != null) {
						final String username = this.jwtUtils.getSubject(token);
						if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
							final UserDetails user = this.userDetailsService.loadUserByUsername(username);
							final boolean isValid = this.jwtUtils.isValidToken(token, user.getUsername());
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
		} catch ( SignatureException | MalformedJwtException exception) {

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);

			final Map<String, Object> body = new HashMap<>();
			body.put(ErrorMessageStore.STATUS, HttpServletResponse.SC_NOT_ACCEPTABLE);
			body.put(ErrorMessageStore.ERROR, ErrorMessageStore.UNAUTHORIZED);
			body.put(ErrorMessageStore.MESSAGE, exception.getMessage());
			body.put(ErrorMessageStore.PATH, request.getServletPath());

			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);

		} catch (ExpiredJwtException jwtException) {

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);

			final Map<String, Object> body = new HashMap<>();
			body.put(ErrorMessageStore.STATUS, HttpServletResponse.SC_NOT_ACCEPTABLE);
			body.put(ErrorMessageStore.ERROR, ErrorMessageStore.UNAUTHORIZED);
			body.put(ErrorMessageStore.MESSAGE, "Access Token Expired");
			body.put(ErrorMessageStore.PATH, request.getServletPath());

			final ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(), body);
		}
	}
}
