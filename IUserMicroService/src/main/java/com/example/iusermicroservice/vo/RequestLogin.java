package com.example.iusermicroservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLogin {
	@Email
	@NotNull(message = "Email cannot be null")
	@Size(min = 2, message = "Email must not be less than 2 characters")
	private String email;

	@NotNull
	@Size(min = 8, message = "Password must not be less than 8 characters")
	private String password;
}
