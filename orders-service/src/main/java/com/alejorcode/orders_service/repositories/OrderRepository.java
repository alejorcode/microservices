package com.alejorcode.orders_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alejorcode.orders_service.model.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
