package com.example.iusermicroservice.config;

import com.example.iusermicroservice.dto.UserDto;
import com.example.iusermicroservice.service.UserService;
import com.example.iusermicroservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Component
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final Environment env;
	private final ObjectMapper objectMapper;
	private final UserService userService;

	public AuthenticationFilter(AuthenticationManager authenticationManager,
								Environment env,
	                            ObjectMapper objectMapper,
	                            UserService userService) {
		super(authenticationManager);
		this.env = env;
		this.objectMapper = objectMapper;
		this.userService = userService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
	                                            HttpServletResponse response) throws AuthenticationException {
		try {
			RequestLogin requestLogin = objectMapper.readValue(request.getInputStream(), RequestLogin.class);
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(requestLogin.getEmail(), requestLogin.getPassword(), new ArrayList<>());
			return getAuthenticationManager().authenticate(token);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
	                                        HttpServletResponse response,
	                                        FilterChain chain,
	                                        Authentication authResult) throws IOException, ServletException {
		String username = ((User) authResult.getPrincipal()).getUsername();
		log.info("username {}", username);
		UserDto userByEmail = userService.getUserByEmail(username);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiration = now.plus(env.getProperty("token.expiration_time", Long.class, 60 * 60 * 24L), ChronoUnit.MILLIS);

		// RSA 키 생성
//		KeyPair keyPair = generateRSAKeyPair();
//		PrivateKey privateKey = keyPair.getPrivate();
//		PublicKey publicKey = keyPair.getPublic();

		// JWT 토큰 생성
		String token = Jwts.builder()
		                   .subject(userByEmail.getUserId())
		                   .expiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
		                   .signWith(Keys.hmacShaKeyFor(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS512) // HS512 방식 (대칭키)
//		                   .signWith(privateKey, Jwts.SIG.RS512) // RSA 방식 (비대칭키)
		                   .compact();

		// JWT 토큰 검증
//		String subject = Jwts.parser()
//		                     .verifyWith(Keys.hmacShaKeyFor(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8))) // HS512 방식
//		                     .verifyWith(publicKey) // RSA 방식
//		                     .build()
//		                     .parseSignedClaims(token)
//		                     .getPayload()
//		                     .getSubject();

		response.addHeader("token", token);
		response.addHeader("userId", userByEmail.getUserId());
	}

	private KeyPair generateRSAKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
