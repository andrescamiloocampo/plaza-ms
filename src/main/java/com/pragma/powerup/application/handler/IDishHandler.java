package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.application.dto.request.DishRequestDto;

public interface IDishHandler {
    void saveDish(DishRequestDto dishRequestDto, int userId);
    void updateDish(int id, int userId, DishPartialUpdateDTO dishPartialUpdateDTO);
}
