package com.example.ieurekadiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class IEurekaDiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IEurekaDiscoveryServiceApplication.class, args);
	}

}
