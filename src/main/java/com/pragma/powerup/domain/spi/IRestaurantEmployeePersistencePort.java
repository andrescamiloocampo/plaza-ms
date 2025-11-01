package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;

import java.util.List;

public interface IRestaurantEmployeePersistencePort {
    void assignEmployeeToRestaurant(RestaurantEmployeeModel restaurantEmployeeModel);
    RestaurantEmployeeModel findByUserId(int userId);
    List<RestaurantEmployeeModel> findAll();
}
