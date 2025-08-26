package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantEmployeeRequestDto {
    private int userId;
    private int restaurantId;
    private boolean active;
}
