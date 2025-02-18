package com.example.iordermicroservice.controller;

import com.example.iordermicroservice.dto.OrderDto;
import com.example.iordermicroservice.service.OrderService;
import com.example.iordermicroservice.vo.RequestOrder;
import com.example.iordermicroservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@GetMapping("/health_check")
	public String status() {
		return "I'm running!";
	}

	@PostMapping("/{userId}/orders")
	public ResponseEntity<ResponseOrder> createOrder(@RequestBody RequestOrder requestOrder,
	                                                 @PathVariable String userId) {
		OrderDto req = OrderDto.from(requestOrder);
		req.setUserId(userId);
		OrderDto order = orderService.createOrder(req);
		return ResponseEntity.status(HttpStatus.CREATED)
		                     .body(ResponseOrder.of(order));
	}

	@GetMapping("/{userId}/orders")
	public ResponseEntity<List<ResponseOrder>> getOrders(@PathVariable String userId) {
		List<ResponseOrder> response = orderService.getOrdersByUserId(userId)
		                                           .stream()
		                                           .map(ResponseOrder::from)
		                                           .toList();
		return ResponseEntity.ok(response);
	}
}
