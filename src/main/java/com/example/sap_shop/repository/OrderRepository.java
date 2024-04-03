package com.example.sap_shop.repository;

import com.example.sap_shop.model.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
