package com.pragma.powerup.domain.spi;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.domain.model.DishModel;

public interface IDishPersistencePort {
    void saveDish(DishModel dishModel);
    void updateDish(int id, DishPartialUpdateDTO dishPartialUpdateDTO);
    void updateDishState(int id, boolean state);
    DishModel findDishById(int id);
}
