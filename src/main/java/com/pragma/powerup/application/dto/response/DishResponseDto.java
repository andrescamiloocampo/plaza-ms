package com.pragma.powerup.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DishResponseDto {
    private int id;
    private String name;
    private int categoryId;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private boolean active;
    private int restaurantId;
}
