package com.pragma.powerup.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantEmployeeResponseDto {
    private int id;
    private int userId;
    private int restaurantId;
    private boolean active;
}
