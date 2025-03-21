package com.example.iusermicroservice.service.impl;

import com.example.iusermicroservice.dto.UserDto;
import com.example.iusermicroservice.entity.UserEntity;
import com.example.iusermicroservice.repository.UserRepository;
import com.example.iusermicroservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDto createUser(UserDto userDto) {
		userDto.setUserId(UUID.randomUUID().toString());
		UserEntity from = UserEntity.from(userDto);
		from.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd()));
		UserEntity saved = userRepository.save(from);
		return UserDto.of(saved);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity user = userRepository.findByUserId(userId)
		                                .orElseThrow(() -> new UsernameNotFoundException("not found"));
		UserDto userDto = UserDto.of(user);
		userDto.setOrders(List.of());
		return userDto;
	}

	@Override
	public UserDto getUserByEmail(String email) {
		UserEntity user = userRepository.findByEmail(email)
		                                .orElseThrow(() -> new UsernameNotFoundException("not found"));
		UserDto userDto = UserDto.of(user);
		userDto.setOrders(List.of());
		return userDto;
	}


	@Override
	public List<UserEntity> getUsersByAll() {
		return userRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity entity = userRepository.findByEmail(username)
		                                  .orElseThrow(() -> new UsernameNotFoundException("user not found"));
		return new User(entity.getEmail(),
		                entity.getEncryptedPwd(),
		                true,
		                true,
		                true,
		                true,
		                new ArrayList<>());
	}
}
