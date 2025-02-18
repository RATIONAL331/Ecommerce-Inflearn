package com.example.iusermicroservice.vo;

import com.example.iusermicroservice.dto.UserDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponseUser {
	private String email;
	private String name;
	private String userId;
	private List<ResponseOrder> orders;

	public static ResponseUser from(UserDto userDto) {
		ResponseUser responseUser = new ResponseUser();
		responseUser.email = userDto.getEmail();
		responseUser.name = userDto.getName();
		responseUser.userId = userDto.getUserId();
		return responseUser;
	}
}
