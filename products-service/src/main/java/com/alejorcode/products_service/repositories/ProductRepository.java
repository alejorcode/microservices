package com.alejorcode.products_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alejorcode.products_service.model.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
