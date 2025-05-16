package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.taskmanager.filters.JWTAuthFilter;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.utils.JWTUtils;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
		
	    // Security 的設定：定義安全規則與 JWT Filter
		@Bean
		protected SecurityFilterChain filterChain(HttpSecurity http, JWTAuthFilter jwtAuthFilter) throws Exception {
			http
		    .authorizeHttpRequests(auth -> auth
		        .requestMatchers(  // 設定哪些路徑
		            "/v3/api-docs/**",
		            "/swagger-ui.html",
		            "/swagger-ui/**",
		            "/webjars/**"
		        ).permitAll() // Swagger 相關的 API 文件路徑 這些路徑都不需要驗證
		        .requestMatchers("/h2-console/**").permitAll() // H2 資料庫控制台  不需要驗證（開發階段用）
		        .requestMatchers("/auth/**").permitAll() // 登入、註冊相關的 API  不需要驗證
		        .anyRequest().authenticated() // 其餘的路徑都需要認證
		    )
		    .anonymous(anonymous -> anonymous.disable()) // 關閉匿名使用者功能（沒有登入的直接拒絕，不給匿名身分）
		    // 若沒有登入就打 API，回傳 401 Unauthorized
		    .exceptionHandling(exception -> 
		                            exception.authenticationEntryPoint((request, response, authException) -> {
		                            	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		                            })
		    )
		    // 在驗證帳密之前，先走我們自訂的 JWT 過濾器
		    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
		    // 禁用 X-Frame-Options header，這是為了允許 H2 資料庫控制台顯示
		    .headers(headers -> headers.frameOptions(FrameOptions -> FrameOptions.disable())) 
		    // 關閉 CSRF因為是用 JWT，不是 session/cookie，不需要跨站保護
		    .csrf(csrf -> csrf.disable());
			
			return http.build();
		}
		
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
		// 註冊自訂的 JWT Filter，讓 Spring Security 可以注入
		@Bean
		protected JWTAuthFilter jwtAuthFilter(JWTUtils jwtUtils, UserService userService) {
			return new JWTAuthFilter(jwtUtils, userService);
		}
		
		// 開發早期可以用這個測試帳號，不用真的註冊
//		@Bean
//		protected UserDetailsService users(PasswordEncoder encoder) {
//			UserDetails user = User.builder()
//	                   .username("user")
//	                   .password(encoder.encode("user"))
//	                   .roles("USER")
//	                   .build();
//		
//		// 使用 InMemoryUserDetailsManager 註冊這個使用者，存在記憶體中
//	    return new InMemoryUserDetailsManager(user);
//		}
}
