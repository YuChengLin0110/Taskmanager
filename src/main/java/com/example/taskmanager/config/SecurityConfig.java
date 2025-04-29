package com.example.taskmanager.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

		@Bean
		protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http
		    .authorizeHttpRequests(auth -> auth
		        .requestMatchers(  // 設定哪些路徑
		            "/v3/api-docs/**",
		            "/swagger-ui.html",
		            "/swagger-ui/**",
		            "/webjars/**"
		        ).permitAll() // 這些路徑都不需要驗證
		        .requestMatchers("/h2-console/**").permitAll()
		        .requestMatchers("/auth/**").permitAll()
		        .anyRequest().authenticated() // 其餘的路徑都需要認證
		    )
		    // 禁用 X-Frame-Options header，這是為了允許 H2 資料庫控制台顯示
		    .headers(headers -> headers.frameOptions(FrameOptions -> FrameOptions.disable())) 
		    // 關閉 CSRF 保護：因為本專案是純 REST API，不用 HTML 表單 + cookie 認證
		    // 而是使用 JWT ，所以不需要
		    .csrf(csrf -> csrf.disable())
		    .httpBasic(withDefaults()); // 使用 HTTP 基本認證（彈窗驗證，目前測試用）
			
			return http.build();
		}
		
		@Bean
		protected PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
		// 建立一個假的使用者帳號，方便測試使用
		@Bean
		protected UserDetailsService users(PasswordEncoder encoder) {
			UserDetails user = User.builder()
	                   .username("user")
	                   .password(encoder.encode("user"))
	                   .roles("USER")
	                   .build();
		
		// 使用 InMemoryUserDetailsManager 註冊這個使用者，存在記憶體中
	    return new InMemoryUserDetailsManager(user);
		}
}
