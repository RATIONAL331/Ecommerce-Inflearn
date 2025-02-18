package com.example.iusermicroservice.entity;

import com.example.iusermicroservice.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String name;
	private String userId;

	@Setter
	private String encryptedPwd;

	public static UserEntity from(UserDto userDto) {
		return UserEntity.builder()
		                 .email(userDto.getEmail())
		                 .name(userDto.getName())
		                 .userId(userDto.getUserId())
		                 .encryptedPwd(userDto.getEncryptedPwd())
		                 .build();
	}
}
