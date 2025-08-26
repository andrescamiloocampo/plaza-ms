package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.RestaurantEmployeeRequestDto;

public interface IRestaurantEmployeeHandler {
    void assignEmployeeToRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto);
}
