package com.alejorcode.orders_service.services;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.alejorcode.orders_service.events.OrderEvent;
import com.alejorcode.orders_service.model.dtos.BaseResponse;
import com.alejorcode.orders_service.model.dtos.OrderItemRequest;
import com.alejorcode.orders_service.model.dtos.OrderItemsResponse;
import com.alejorcode.orders_service.model.dtos.OrderRequest;
import com.alejorcode.orders_service.model.dtos.OrderResponse;
import com.alejorcode.orders_service.model.entities.Order;
import com.alejorcode.orders_service.model.entities.OrderItems;
import com.alejorcode.orders_service.model.enums.OrderStatus;
import com.alejorcode.orders_service.repositories.OrderRepository;
import com.alejorcode.orders_service.utils.JsonUtils;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderService {

	private final OrderRepository orderRepository;

	private final WebClient.Builder webClientBuilder;
	
	private final KafkaTemplate<String, String> kafkaTemplate;
	
	private final ObservationRegistry observationRegistry;

	public OrderResponse placeOrder(OrderRequest orderRequest) {
		
		Observation inventoryObservation=Observation.createNotStarted("inventory-service", observationRegistry);
		
		return inventoryObservation.observe(() -> {
            BaseResponse result = this.webClientBuilder.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();
            if (result != null && !result.hasErrors()) {
                Order order = new Order();
                order.setOrderNumber(UUID.randomUUID().toString());
                order.setOrderItems(orderRequest.getOrderItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequestToOrderItem(orderItemRequest, order))
                    .toList());
                var savedOrder = this.orderRepository.save(order);
            
                this.kafkaTemplate.send("orders-topic", JsonUtils.toJson(
                    new OrderEvent(savedOrder.getOrderNumber(), savedOrder.getOrderItems().size(), OrderStatus.PLACED)
               ));
            
                return mapToOrderResponse(savedOrder);
               } else {
                  throw new IllegalArgumentException("Some of the products are not in stock");
            }
	    });
     }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders.stream().map(this::mapToOrderResponse).toList();

    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber()
                , order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItems orderItems) {
        return new OrderItemsResponse(orderItems.getId(), orderItems.getSku(), orderItems.getPrice(), orderItems.getQuantity());
    }

    private OrderItems mapOrderItemRequestToOrderItem(OrderItemRequest orderItemRequest, Order order) {
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
