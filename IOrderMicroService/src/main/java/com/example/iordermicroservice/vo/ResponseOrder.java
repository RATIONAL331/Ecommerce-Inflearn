package com.example.iordermicroservice.vo;

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
}
