package com.example.iusermicroservice.dto;

import com.example.iusermicroservice.entity.UserEntity;
import com.example.iusermicroservice.vo.RequestUser;
import com.example.iusermicroservice.vo.ResponseOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDto {
	private String email;
	private String pwd;
	private String name;
	private String userId;
	private LocalDateTime createdAt;
	private List<ResponseOrder> orders;

	private String encryptedPwd;

	public static UserDto from(RequestUser requestUser) {
		UserDto userDto = new UserDto();
		userDto.setEmail(requestUser.getEmail());
		userDto.setPwd(requestUser.getPwd());
		userDto.setName(requestUser.getName());
		return userDto;
	}

	public static UserDto of(UserEntity saved) {
		UserDto userDto = new UserDto();
		userDto.setEmail(saved.getEmail());
		userDto.setName(saved.getName());
		userDto.setUserId(saved.getUserId());
		userDto.setCreatedAt(LocalDateTime.now());
		userDto.setEncryptedPwd(saved.getEncryptedPwd());
		return userDto;
	}
}
