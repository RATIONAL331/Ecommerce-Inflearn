# Eureka Server
* `@EnableEurekaServer`로 선언

```yaml
eureka:
  client:
    fetch-registry: false
    register-with-eureka: false # 기본적으로 Eureka Library는 자기 자신을 Eureka Client 등록하는 작업을 진행하게 되는데 이를 방지
```

# Eureka Client
* `@EnableDiscoveryClient`로 선언

```yaml
eureka:
  instance:
    instance-id: ${spring.application.hostname}:${spring.application.instance_id}:${random.value}} # port를 0으로 지정하였을 때 자동 포트
  client:
    register-with-eureka: true
    fetch-registry: true # Eureka Server로 부터 인스턴스들의 정보를 주기적으로 가져올 것인지를 설정하는 속성 true일 시 갱신된 정보를 받는다
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka
```

# Gateway
## Reactive
```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081
          predicates:
            - Path=/first-service/**
        - id: second-service
          uri: http://localhost:8082
          predicates:
            - Path=/second-service/**
```
* 기본적으로 Netty에서 수행

## MVC
```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-gateway-mvc</artifactId>
</dependency>
```
```yaml
spring:
  cloud:
    gateway:
      mvc:
        routes:
          - id: first-service
            uri: http://localhost:8081
            predicates:
              - Path=/first-service/**
          - id: second-service
            uri: http://localhost:8082
            predicates:
              - Path=/second-service/**
```
* 톰캣 서버에서 수행
## 위의점
* gateway를 통해서 전달하는 경우 기본적으로 요청한 path 그대로 전달됨
  * 예를 들어서 gateway 주소가 localhost:8000, first-service 주소가 localhost:8081일 경우
  * http://localhost:8000/first-service/welcome 으로 접속한 경우 http://localhost:8081/first-service/welcome 을 요청
 
# Gateway Filter
## Reactive
```java
@Component
public class FilterConfig {
	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
		              .route(r -> r.path("/first-service/**")
		                           .filters(f -> f.addRequestHeader("first-request", "first-request-header")
		                                          .addResponseHeader("first-response", "first-response-header"))
		                           .uri("http://localhost:8081"))
				      .route(r -> r.path("/second-service/**")
		                           .filters(f -> f.addRequestHeader("second-request", "second-request-header")
		                                          .addResponseHeader("second-response", "second-response-header"))
		                           .uri("http://localhost:8082"))
				      .build();
	}
}
```
## Reactive YAML
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081
          predicates:
            - Path=/first-service/**
          filters:
            - AddRequestHeader=first-request, first-request-header
            - AddResponseHeader=first-response, first-response-header
        - id: second-service
          uri: http://localhost:8082
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=first-request, first-request-header
            - AddResponseHeader=first-response, first-response-header
```

## MVC
```java
@Component
public class FilterConfig {
	@Bean
	public RouterFunction<ServerResponse> routeConfig() {
		return RouterFunctions.route()
		                      .nest(RequestPredicates.path("/first-service/**"), 
		                            builder -> builder.GET(HandlerFunctions.http("http://localhost:8081"))
		                                              .before(BeforeFilterFunctions.addRequestHeader("first-request", "first-request-header"))
		                                              .after(AfterFilterFunctions.addResponseHeader("first-response", "first-response-header")))
		                      .nest(RequestPredicates.path("/second-service/**"),
		                            builder -> builder.GET(HandlerFunctions.http("http://localhost:8082"))
		                                              .before(BeforeFilterFunctions.addRequestHeader("second-request", "second-request-header"))
		                                              .after(AfterFilterFunctions.addResponseHeader("second-response", "second-response-header")))
		                      .build();
	}
}
```
```java
@Component
public class FilterConfig {
	@Bean
	public RouterFunction<ServerResponse> routeConfig() {
		return GatewayRouterFunctions.route("first-service")
		                             .route(GatewayRequestPredicates.path("/first-service/**"), HandlerFunctions.http("http://localhost:8081"))
		                             .filter(HandlerFilterFunction.ofRequestProcessor(BeforeFilterFunctions.addRequestHeader("first-request", "first-request-header")))
		                             .filter(HandlerFilterFunction.ofResponseProcessor(AfterFilterFunctions.addResponseHeader("first-response", "first-response-header")))
		                             .build()
		                             .and(GatewayRouterFunctions.route("second-service")
		                                                        .route(GatewayRequestPredicates.path("/second-service/**"), HandlerFunctions.http("http://localhost:8082"))
		                                                        .filter(HandlerFilterFunction.ofRequestProcessor(BeforeFilterFunctions.addRequestHeader("second-request", "second-request-header")))
		                                                        .filter(HandlerFilterFunction.ofResponseProcessor(AfterFilterFunctions.addResponseHeader("second-response", "second-response-header")))
		                                                        .build());
	}
}
```

# Gateway Custom Filter
## Java
```java
public class CustomFilter implements GatewayFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		
		return Mono.fromRunnable(() -> {
			System.out.println("Custom Pre Filter: request id -> " + request.getId());
		});
	}
}
```
```java
return builder.routes()
	.route(r -> r.path("/first-service/**")
	.filters(f -> f.filter(new CustomFilter()))
	.uri("http://localhost:8081"))
	.route(r -> r.path("/second-service/**")
	.filters(f -> f.filter(new CustomFilter())) // custom filter 적용
	.uri("http://localhost:8082"))
	.build();
```
## YAML
```java
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
	public CustomFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				System.out.println("Custom Pre Filter: request id -> " + request.getId());
			}));
		};
	}

        @Data
	public static class Config {
            private String message;
	}
}
```
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081
          predicates:
            - Path=/first-service/**
          filters:
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082
          predicates:
            - Path=/second-service/**
          filters:
            - name: XXXFilter
            - name: CustomFilter
              args:
                message: Hello
```
# Gateway Global Filter
```java
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
	public GlobalFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				System.out.println("Custom Pre Filter: request id -> " + request.getId());
			}));
		};
	}

	@Data
	public static class Config {
		private String baseMessage;
		private boolean preLogger;
		private boolean postLogger;
	}
}
```
```yaml
spring:
  cloud:
    gateway:
      default-filters: # 글로벌 필터 적용
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud
            preLogger: true
            postLogger: true
      routes:
        - id: first-service
          uri: http://localhost:8081
          predicates:
            - Path=/first-service/**
          filters:
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082
          predicates:
            - Path=/second-service/**
          filters:
            - CustomFilter
```
# Gateway Load Balancer (LB)
```yaml
spring:
  cloud:
    gateway:
      default-filters:
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud
            preLogger: true
            postLogger: true
      routes:
        - id: first-service
          uri: lb://MY-FIRST-SERVICE # 유레카에 등록되어있는 서비스 이름 (IP 주소가 아닌 서비스 이름)
          predicates:
            - Path=/first-service/**
          filters:
            - CustomFilter
        - id: second-service
          uri: lb://MY-SECOND-SERVICE # 유레카에 등록되어있는 서비스 이름
          predicates:
            - Path=/second-service/**
          filters:
            - CustomFilter
```
* IP 주소 대신 서비스 이름을 이용하여 Route 가능

# User Service Security Config
```java
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
```
