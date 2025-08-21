package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;

import java.util.List;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto, int userId);

    void updateDish(int id, int userId, DishPartialUpdateDTO dishPartialUpdateDTO);

    void updateDishState(int id, int userId, boolean state);

    List<DishResponseDto> getDishes(int restaurantId, int page, int size, String category);
}
