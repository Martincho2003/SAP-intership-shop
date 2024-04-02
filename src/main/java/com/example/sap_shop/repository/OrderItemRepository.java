package com.example.sap_shop.repository;

import com.example.sap_shop.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {
}
