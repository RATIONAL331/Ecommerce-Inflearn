package com.example.iusermicroservice.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.application")
public class Greeting {
	private String name;
}
