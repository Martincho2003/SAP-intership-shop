package com.example.sap_shop.repository;

import com.example.sap_shop.model.OrderItem;
import com.example.sap_shop.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    OrderItem findByProduct(Product product);
}
