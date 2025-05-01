package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	
	// 定義一個 JWT 驗證規則 的名字
	private final String securitySchemeName = "bearerAuth";
	
	@Bean
	protected OpenAPI customSwaggerConfig() {
		return new OpenAPI()
				// 告訴 Swagger： 預設所有 API 都需要帶上 JWT token
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				 // 定義 token 格式 規則 
				.components(new Components()
						.addSecuritySchemes(securitySchemeName, new SecurityScheme()
																		.name("Authorization") // token 放在 Header 名字叫 Authorization
																		.type(SecurityScheme.Type.HTTP) // 用 HTTP 的方式傳 token
																		.scheme("bearer") // 用 bearer token 的方式
																		.bearerFormat("JWT"))); // token 是 JWT 格式
	} 
}
