package com.example.taskmanager.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 原本 JWTUtils 是設計成 static 型式，secretKey 也是 static，並透過 @Value 在初始化時注入值，搭配 @PostConstruct 來確保 key 正確設置
 * 但這樣會有問題：Spring 注入 static 變數的時機不穩定
 * 在某些情況下 (例如 Filter 很早就使用該類別) ，secretKey 可能還沒被注入，導致為 null
 * 這樣在解析 JWT 時就會拋出 SignatureException
 *
 * 解決方法是：移除 static 變數，將 JWTUtils 改為 Spring 管理的元件 (加上 @Component)
 * 並透過建構子注入 secretKey ，這樣 Spring 在初始化時會確保所需的值都已經設置好
 * 之後在任何地方使用都不會再有這個問題。
 */
@Component
public class JWTUtils {
	
	private final Key signKey;
	
	@Value("${jwt.secretKey}")
	private String secretKeyValue;
	
	public JWTUtils(@Value("${jwt.secretKey}") String secretKeyValue) {
		this.signKey = new SecretKeySpec(secretKeyValue.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
	}

	public String generateToken(String username) {

		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000))
				.signWith(signKey)
				.compact();
	}

	public String getSubject(String token) {
		JwtParser parser = Jwts.parserBuilder().setSigningKey(signKey).build();
		
		String subject = parser.parseClaimsJws(token).getBody().getSubject();
		
		return subject;
	}
	
	public boolean isTokenExpired(String token) {
		
		return getExpirarion(token).before(new Date());
	}
	
	public boolean validateToken(String token, String username) {
		
		return username.equals(getSubject(token)) && !isTokenExpired(token);
	}
	
	private Date getExpirarion(String token) {
		JwtParser parser = Jwts.parserBuilder().setSigningKey(signKey).build();
		
		Date expiration = parser.parseClaimsJws(token).getBody().getExpiration();
		
		return expiration;
	}
}
