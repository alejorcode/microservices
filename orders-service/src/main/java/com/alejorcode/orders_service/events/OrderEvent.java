package com.alejorcode.orders_service.events;

import com.alejorcode.orders_service.model.enums.OrderStatus;

public record OrderEvent(String orderNumber, int itemsCount, OrderStatus orderStatus) {

}
