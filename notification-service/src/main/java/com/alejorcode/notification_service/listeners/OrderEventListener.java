package com.alejorcode.notification_service.listeners;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alejorcode.notification_service.events.OrderEvent;
import com.alejorcode.notification_service.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderEventListener {
	
	@KafkaListener(topics="orders-topic")
	public void handleOrdersNotifications(String message) {
		var orderEvent= JsonUtils.fromJson(message, OrderEvent.class);
		
		log.info("Order {} event received for order: {} with {} items", orderEvent.orderStatus(), orderEvent.orderNumber(), orderEvent.itemsCount());
	}

}
