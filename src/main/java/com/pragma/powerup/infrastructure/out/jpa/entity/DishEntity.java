package com.pragma.powerup.infrastructure.out.jpa.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dishes")
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name",nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id",nullable = false)
    private CategoryEntity category;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;
}
