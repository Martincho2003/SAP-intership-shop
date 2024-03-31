package com.example.sap_shop.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
