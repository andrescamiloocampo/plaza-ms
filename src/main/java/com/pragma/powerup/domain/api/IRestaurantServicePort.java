package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.List;

public interface IRestaurantServicePort {
    void saveRestaurant(RestaurantModel restaurantModel);
    RestaurantModel getRestaurantById(int id);
    List<RestaurantModel> getRestaurantByOwnerId(int ownerId);
    boolean getOwnership(int id, int ownerId);
    List<RestaurantModel> getRestaurants(int page,int size);
}
