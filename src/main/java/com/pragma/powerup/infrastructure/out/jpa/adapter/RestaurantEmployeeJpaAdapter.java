package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantEmployeeJpaAdapter implements IRestaurantEmployeePersistencePort {

    private final IRestaurantEmployeeRepository restaurantEmployeeRepository;
    private final IRestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;

    @Override
    public void assignEmployeeToRestaurant(RestaurantEmployeeModel restaurantEmployeeModel) {
        RestaurantEmployeeModel existingAssignment = findByUserId(restaurantEmployeeModel.getUserId());
        if (existingAssignment != null) {
            existingAssignment.setActive(restaurantEmployeeModel.isActive());
            existingAssignment.setRestaurantId(restaurantEmployeeModel.getRestaurantId());
            restaurantEmployeeRepository.save(restaurantEmployeeEntityMapper.toEntity(existingAssignment));
        } else {
            restaurantEmployeeRepository.save(restaurantEmployeeEntityMapper.toEntity(restaurantEmployeeModel));
        }
    }

    @Override
    public RestaurantEmployeeModel findByUserId(int userId) {
        return restaurantEmployeeEntityMapper
                .toModel(restaurantEmployeeRepository
                        .findByUserIdAndActiveTrue(userId)
                        .orElse(null));
    }

    @Override
    public List<RestaurantEmployeeModel> findAll() {
        return restaurantEmployeeEntityMapper
                .toModelList(restaurantEmployeeRepository.findAll());
    }
}
