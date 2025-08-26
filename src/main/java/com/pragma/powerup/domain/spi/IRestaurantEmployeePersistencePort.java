package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;

public interface IRestaurantEmployeePersistencePort {
    void assignEmployeeToRestaurant(RestaurantEmployeeModel restaurantEmployeeModel);
    RestaurantEmployeeModel findByUserId(int userId);
}
