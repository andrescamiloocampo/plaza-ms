package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;

public interface IRestaurantEmployeeServicePort {
    void assignEmployeeToRestaurant(RestaurantEmployeeModel restaurantEmployeeModel);
}
