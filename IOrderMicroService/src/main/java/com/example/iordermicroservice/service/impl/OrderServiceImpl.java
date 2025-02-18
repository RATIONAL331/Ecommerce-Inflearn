package com.example.iordermicroservice.service.impl;

import com.example.iordermicroservice.dto.OrderDto;
import com.example.iordermicroservice.entity.OrderEntity;
import com.example.iordermicroservice.repository.OrderRepository;
import com.example.iordermicroservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;

	@Override
	public OrderDto createOrder(OrderDto orderDto) {
		orderDto.setOrderId(UUID.randomUUID().toString());
		orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());
		OrderEntity from = OrderEntity.from(orderDto);
		OrderEntity saved = orderRepository.save(from);

		return OrderDto.of(saved);
	}

	@Override
	public OrderDto getOrderByOrderId(String orderId) {
		return orderRepository.findByOrderId(orderId)
		                      .map(OrderDto::of)
		                      .orElseThrow(() -> new RuntimeException("not found"));
	}

	@Override
	public List<OrderEntity> getOrdersByUserId(String userId) {
		return orderRepository.findByUserId(userId);
	}
}
