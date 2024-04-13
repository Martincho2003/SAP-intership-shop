package com.example.sap_shop.repository;

import com.example.sap_shop.model.ShoppingCart;
import com.example.sap_shop.model.User;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, Long> {
    ShoppingCart findByUser(User user);
}
