package com.pragma.powerup.domain.api;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.domain.model.DishModel;

import java.util.List;

public interface IDishServicePort {
    void saveDish(DishModel dishModel, int userId);
    void updateDish(int id, int userId, DishPartialUpdateDTO dishPartialUpdateDTO);
    void updateDishState(int id, int userId, boolean state);
    List<DishModel> getDishes(int restaurantId,int page,int size,String category);
}
