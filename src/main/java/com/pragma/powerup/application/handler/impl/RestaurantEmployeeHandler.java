package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantEmployeeRequestDto;
import com.pragma.powerup.application.handler.IRestaurantEmployeeHandler;
import com.pragma.powerup.application.mapper.request.IRestaurantEmployeeRequestMapper;
import com.pragma.powerup.domain.api.IRestaurantEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantEmployeeHandler implements IRestaurantEmployeeHandler {
    private final IRestaurantEmployeeServicePort restaurantEmployeeServicePort;
    private final IRestaurantEmployeeRequestMapper restaurantEmployeeRequestMapper;

    @Override
    public void assignEmployeeToRestaurant(RestaurantEmployeeRequestDto restaurantEmployeeRequestDto) {
        restaurantEmployeeServicePort.assignEmployeeToRestaurant(restaurantEmployeeRequestMapper.toRestaurantEmployeeModel(restaurantEmployeeRequestDto));
    }
}
