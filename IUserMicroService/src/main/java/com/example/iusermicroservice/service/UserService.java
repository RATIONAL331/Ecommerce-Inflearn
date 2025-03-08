package com.example.iusermicroservice.service;

import com.example.iusermicroservice.dto.UserDto;
import com.example.iusermicroservice.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto userDto);
	UserDto getUserByUserId(String userId);
	List<UserEntity> getUsersByAll();
}
