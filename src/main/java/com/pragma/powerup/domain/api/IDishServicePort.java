package com.pragma.powerup.domain.api;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.domain.model.DishModel;

public interface IDishServicePort {
    void saveDish(DishModel dishModel, int userId);

    void updateDish(int id, int userId, DishPartialUpdateDTO dishPartialUpdateDTO);
}
