package com.pragma.powerup.domain.spi;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDto;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PaginatedDishModel;

import java.util.List;

public interface IDishPersistencePort {
    void saveDish(DishModel dishModel);

    void updateDish(int id, DishPartialUpdateDto dishPartialUpdateDTO);

    void updateDishState(int id, boolean state);

    DishModel findDishById(int id);

    List<DishModel> getDishes(int restaurantId, int page, int size, String category);

    PaginatedDishModel getPaginatedDishes(int restaurantId, int page, int size);
}
