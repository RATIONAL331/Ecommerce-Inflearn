package com.example.igatewayservice.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
	private final Environment env;

	public AuthorizationHeaderFilter(Environment env) {
		super(Config.class);
		this.env = env;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			Optional<String> header = Optional.ofNullable(request.getHeaders().get(HttpHeaders.AUTHORIZATION))
			                                  .flatMap(headers -> headers.stream().findFirst());
			if (header.isEmpty()) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}

			Optional<String> jwt = header.map(authorizationHeader -> authorizationHeader.replace("Bearer ", ""))
			                             .filter(this::isValidToken);

			if (jwt.isEmpty()) {
				return onError(exchange, "invalid token", HttpStatus.UNAUTHORIZED);
			}

			return chain.filter(exchange);
		});
	}

	public static class Config {
		// Put configuration properties here
	}

	private Mono<Void> onError(ServerWebExchange exchange, String errMessage, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);
		log.error(errMessage);
		return response.setComplete();
	}

	private boolean isValidToken(String token) {
		try {
			String subject = Jwts.parser()
			                     .verifyWith(Keys.hmacShaKeyFor(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8)))
			                     .build()
			                     .parseSignedClaims(token)
			                     .getPayload()
			                     .getSubject();
			log.info("subject: {}", subject);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}
}
