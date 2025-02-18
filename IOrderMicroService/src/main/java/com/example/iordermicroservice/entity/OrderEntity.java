package com.example.iordermicroservice.entity;

import com.example.iordermicroservice.dto.OrderDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String productId;
	private Integer qty;
	private Integer unitPrice;
	private Integer totalPrice;

	private String userId;
	private String orderId;

	private LocalDateTime createdAt;

	public static OrderEntity from(OrderDto orderDto) {
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.productId = orderDto.getProductId();
		orderEntity.qty = orderDto.getQty();
		orderEntity.unitPrice = orderDto.getUnitPrice();
		orderEntity.totalPrice = orderDto.getTotalPrice();
		orderEntity.userId = orderDto.getUserId();
		orderEntity.orderId = orderDto.getOrderId();
		orderEntity.createdAt = LocalDateTime.now();
		return orderEntity;
	}
}
