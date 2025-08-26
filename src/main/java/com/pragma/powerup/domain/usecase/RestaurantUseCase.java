package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.infrastructure.out.feign.dto.response.UserResponseDto;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserAuthClientPort;

import java.util.List;

public class RestaurantUseCase implements IRestaurantServicePort {
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserAuthClientPort userAuthClientPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IUserAuthClientPort userAuthClientPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userAuthClientPort = userAuthClientPort;
    }

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel) {

        UserResponseDto user = this.userAuthClientPort.getUserById("" + restaurantModel.getOwnerId());

        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("OWNER"))) {
            throw new InvalidRoleException();
        }

        if (!restaurantModel.getNit().matches("\\d+")) {
            throw new InvalidNitException();
        }

        if (!restaurantModel.getPhone().matches("^\\+?\\d{1,13}$")) {
            throw new InvalidPhoneException();
        }

        if (restaurantModel.getName().matches("\\d+")) {
            throw new InvalidNameException();
        }

        this.restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    @Override
    public RestaurantModel getRestaurantById(int id) {
        return restaurantPersistencePort.getRestaurantById(id);
    }

    @Override
    public List<RestaurantModel> getRestaurantByOwnerId(int ownerId) {
        return restaurantPersistencePort.getRestaurantsByOwnerId(ownerId);
    }

    @Override
    public List<RestaurantModel> getRestaurants(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException();
        }

        if (size <= 0) {
            throw new IllegalArgumentException();
        }

        return restaurantPersistencePort.getRestaurants(page, size);
    }

    @Override
    public boolean getOwnership(int id, int ownerId) {
        return restaurantPersistencePort.getOwnership(id, ownerId);
    }

}
