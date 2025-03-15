package com.example.iusermicroservice.config;

import com.example.iusermicroservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
	private static final String[] WHITE_LIST = {
			"/health_check",
			"/welcome",
			"/users/**"
	};

	private final Environment environment;
	private final ObjectMapper objectMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder);
		provider.setUserDetailsService(userService);
		return provider;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Bean
	public SecurityFilterChain config(HttpSecurity http,
	                                  AuthenticationConfiguration configuration) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
				.headers(headers -> headers
						.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // X-Frame-Options 비활성화
				)
				.authorizeHttpRequests(authorize -> authorize
//						.requestMatchers(WHITE_LIST).permitAll() // 특정 경로 허용
						.requestMatchers(PathRequest.toH2Console()).permitAll()
//						.anyRequest().authenticated() // 나머지 요청은 인증 필요
				)
				.authorizeHttpRequests(req -> req.
						requestMatchers("/actuator/**").permitAll()
				)
				.authorizeHttpRequests(request -> request
						.requestMatchers("/**").access((authentication, ctx) -> {
							String ipAddress = ctx.getRequest().getRemoteAddr();
							if (ipAddress.startsWith("192.168")) {
								ipAddress = "127.0.0.1";
							}
							boolean allowed = new IpAddressMatcher("127.0.0.1").matches(ipAddress);
							return new AuthorizationDecision(allowed);
						}))
				.addFilterBefore(getAuthenticationFilter(configuration), UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	private AuthenticationFilter getAuthenticationFilter(AuthenticationConfiguration configuration) throws Exception {
		//		authenticationFilter.setAuthenticationManager(authenticationManager(configuration));
		return new AuthenticationFilter(authenticationManager(configuration), environment, objectMapper, userService);
	}

}
