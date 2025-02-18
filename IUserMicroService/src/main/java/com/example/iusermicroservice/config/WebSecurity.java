package com.example.iusermicroservice.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurity {
	private static final String[] WHITE_LIST = {
			"/health_check",
			"/welcome",
			"/users/**"
	};

	@Bean
	public SecurityFilterChain config(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
				.headers(headers -> headers
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // X-Frame-Options 비활성화
				)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(WHITE_LIST).permitAll() // 특정 경로 허용
						.requestMatchers(PathRequest.toH2Console()).permitAll()
						.anyRequest().authenticated() // 나머지 요청은 인증 필요
				)
				.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
