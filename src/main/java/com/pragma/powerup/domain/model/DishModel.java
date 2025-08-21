package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DishModel {
    private int id;
    private String name;
    private CategoryModel category;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean active;
    private int restaurantId;
}
