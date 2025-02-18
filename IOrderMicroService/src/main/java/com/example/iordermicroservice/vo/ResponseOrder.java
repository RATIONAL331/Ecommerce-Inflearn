package com.example.iordermicroservice.vo;

import com.example.iordermicroservice.dto.OrderDto;
import com.example.iordermicroservice.entity.OrderEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseOrder {
	private String productId;
	private Integer qty;
	private Integer unitPrice;
	private Integer totalPrice;
	private String orderId;

	private LocalDateTime createdAt;

	public static ResponseOrder of(OrderDto dto) {
		ResponseOrder order = new ResponseOrder();
		order.setProductId(dto.getProductId());
		order.setQty(dto.getQty());
		order.setUnitPrice(dto.getUnitPrice());
		order.setTotalPrice(dto.getTotalPrice());
		order.setOrderId(dto.getOrderId());
		return order;
	}

	public static ResponseOrder from(OrderEntity entity) {
		ResponseOrder dto = new ResponseOrder();
		dto.setProductId(entity.getProductId());
		dto.setQty(entity.getQty());
		dto.setUnitPrice(entity.getUnitPrice());
		dto.setTotalPrice(entity.getTotalPrice());
		dto.setOrderId(entity.getOrderId());
		dto.setCreatedAt(entity.getCreatedAt());
		return dto;
	}
}
