package com.example.sap_shop.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate = new Date();

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;

    @OneToMany
    private List<Product> products;
}
