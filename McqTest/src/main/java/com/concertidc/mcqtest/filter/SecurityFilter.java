package com.concertidc.mcqtest.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.concertidc.mcqtest.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils util;
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Reading Token from Authorization Header
		String token= request.getHeader("Authorization");
		if(token !=null) {
			String username= util.getSubject(token);
			if(username !=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				UserDetails user= userDetailsService.loadUserByUsername(username);
				boolean isValid=util.isValidToken(token, user.getUsername());
				if(isValid) {
					UsernamePasswordAuthenticationToken authToken= 
							new UsernamePasswordAuthenticationToken(username, user.getPassword(), user.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);		
				}
			}
		}
		filterChain.doFilter(request, response);
	}
	
}
