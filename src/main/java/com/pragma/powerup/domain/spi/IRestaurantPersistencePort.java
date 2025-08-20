package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.List;


public interface IRestaurantPersistencePort {
    void saveRestaurant(RestaurantModel restaurantModel);
    RestaurantModel getRestaurantById(int id);
    List<RestaurantModel> getRestaurants(int page, int size);
    boolean getOwnership(int id, int ownerId);
}
