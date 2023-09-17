package com.alejorcode.notification_service.events;

import com.alejorcode.notification_service.model.enums.OrderStatus;

public record OrderEvent(String orderNumber, int itemsCount, OrderStatus orderStatus) {

}
