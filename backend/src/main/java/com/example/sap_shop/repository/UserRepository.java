package com.example.sap_shop.repository;

import com.example.sap_shop.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    User findByEmail(String email);
}
