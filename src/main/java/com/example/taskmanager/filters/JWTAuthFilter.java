package com.example.taskmanager.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.utils.JWTUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// OncePerRequestFilter 每次請求只執行 1 次
public class JWTAuthFilter extends OncePerRequestFilter {
	
	private final JWTUtils jwtUtils;
	private final UserService userService;
	
	@Autowired
	public JWTAuthFilter(JWTUtils jwtUtils, UserService userService) {
		this.jwtUtils = jwtUtils;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		// 如果是 /auth/** 路徑，直接跳過JWT驗證
		if(request.getRequestURI().startsWith("/auth/")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = request.getHeader("Authorization");

		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);

			String username = jwtUtils.getSubject(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				Optional<User> userOpt = userService.findByUsername(username);
				
				if(userOpt.isPresent()) {
					User user = userOpt.get();
					
					// 建立身份認證對象 (用戶信息, 密碼, 權限列表)
					Authentication auth = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
					
					// 設置到 SecurityContextHolder ，這樣 Spring Security 就能識別當前用戶
					SecurityContextHolder.getContext().setAuthentication(auth);
					
				}	
			}
		}

		filterChain.doFilter(request, response);
	}
}
