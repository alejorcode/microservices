package com.alejorcode.orders_service.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.alejorcode.orders_service.model.dtos.OrderRequest;
import com.alejorcode.orders_service.model.dtos.OrderResponse;
import com.alejorcode.orders_service.services.OrderService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/order")
@RestController
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@CircuitBreaker(name = "orders-service", fallbackMethod = "placeOrderFallback")
	public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
		var orders = this.orderService.placeOrder(orderRequest);
		return ResponseEntity.ok(orders);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<OrderResponse> getOrders() {
		return this.orderService.getAllOrders();
	}

	private ResponseEntity<OrderResponse> placeOrderFallback(OrderRequest orderRequest, Throwable throwable) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
	}

}
