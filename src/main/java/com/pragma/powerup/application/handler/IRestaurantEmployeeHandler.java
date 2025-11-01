package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.RestaurantEmployeeRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantEmployeeResponseDto;

import java.util.List;

public interface IRestaurantEmployeeHandler {
    void assignEmployeeToRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto);
    List<RestaurantEmployeeResponseDto> getAllEmployees();
}
