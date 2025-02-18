package com.example.iordermicroservice.service;

import com.example.iordermicroservice.dto.OrderDto;
import com.example.iordermicroservice.entity.OrderEntity;

import java.util.List;

public interface OrderService {
	OrderDto createOrder(OrderDto orderDto);
	OrderDto getOrderByOrderId(String orderId);
	List<OrderEntity> getOrdersByUserId(String userId);
}
