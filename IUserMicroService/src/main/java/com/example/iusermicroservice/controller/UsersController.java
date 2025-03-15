package com.example.iusermicroservice.controller;

import com.example.iusermicroservice.dto.UserDto;
import com.example.iusermicroservice.service.UserService;
import com.example.iusermicroservice.vo.Greeting;
import com.example.iusermicroservice.vo.RequestUser;
import com.example.iusermicroservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UsersController {
	private final Greeting greeting;
	private final UserService userService;
	private final Environment environment;

	@GetMapping("/health_check")
	public String status() {
		return "I'm running!" + environment.getProperty("token.secret") + "\n" + environment.getProperty("token.expiration_time");
	}

	@GetMapping("/welcome")
	public String welcome() {
		return greeting.getName();
	}

	@PostMapping("/users")
	public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
		UserDto userDto = UserDto.from(user);
		UserDto created = userService.createUser(userDto);
		ResponseUser result = ResponseUser.from(created);

		return ResponseEntity.status(HttpStatus.CREATED)
		                     .body(result);
	}

	@GetMapping("/users")
	public ResponseEntity<List<ResponseUser>> getAllUser() {
		List<ResponseUser> users = userService.getUsersByAll()
		                                 .stream()
		                                 .map(UserDto::of)
		                                 .map(ResponseUser::from)
		                                 .toList();

		return ResponseEntity.status(HttpStatus.OK)
		                     .body(users);
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseUser> getUserByUserId(@PathVariable String userId) {
		UserDto userDto = userService.getUserByUserId(userId);
		ResponseUser result = ResponseUser.from(userDto);

		return ResponseEntity.status(HttpStatus.OK)
		                     .body(result);
	}
}
