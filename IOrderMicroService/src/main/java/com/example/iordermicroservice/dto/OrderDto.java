package com.example.iordermicroservice.dto;

import com.example.iordermicroservice.entity.OrderEntity;
import lombok.Data;

@Data
public class OrderDto {
	private String productId;
	private Integer qty;
	private Integer unitPrice;
	private Integer totalPrice;

	private String orderId;
	private String userId;

	public static OrderDto of(OrderEntity saved) {
		OrderDto orderDto = new OrderDto();
		orderDto.setProductId(saved.getProductId());
		orderDto.setQty(saved.getQty());
		orderDto.setUnitPrice(saved.getUnitPrice());
		orderDto.setTotalPrice(saved.getTotalPrice());
		orderDto.setOrderId(saved.getOrderId());
		orderDto.setUserId(saved.getUserId());
		return orderDto;
	}
}
