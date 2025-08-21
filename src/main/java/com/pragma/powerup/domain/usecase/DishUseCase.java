package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.InvalidCategoryException;
import com.pragma.powerup.domain.exception.InvalidOwnerException;
import com.pragma.powerup.domain.exception.InvalidPriceException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.math.BigDecimal;
import java.util.List;

public class DishUseCase implements IDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    public DishUseCase(IDishPersistencePort dishPersistencePort,
                       IRestaurantPersistencePort restaurantPersistencePort,
                       ICategoryPersistencePort categoryPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    public List<DishModel> getDishes(int restaurantId,int page, int size, String category){
        if(page < 0){
            throw new IllegalArgumentException();
        }
        if(size <= 0){
            throw new IllegalArgumentException();
        }
        return dishPersistencePort.getDishes(restaurantId,page,size,category);
    }

    @Override
    public void saveDish(DishModel dishModel, int userId) {
        boolean ownership = restaurantPersistencePort.getOwnership(dishModel.getRestaurantId(), userId);
        boolean isValidCategory = categoryPersistencePort.existsById(dishModel.getCategory().getId());

        if (!ownership) {
            throw new InvalidOwnerException();
        }

        if(!isValidCategory){
            throw new InvalidCategoryException();
        }

        dishPersistencePort.saveDish(dishModel);
    }

    @Override
    public void updateDish(int id,int userId, DishPartialUpdateDTO dishPartialUpdateDTO){
        int restaurantId = dishPersistencePort.findDishById(id).getRestaurantId();
        boolean ownership = restaurantPersistencePort.getOwnership(restaurantId,userId);

        if(!ownership){
            throw new InvalidOwnerException();
        }

        if(dishPartialUpdateDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidPriceException();
        }

        dishPersistencePort.updateDish(id,dishPartialUpdateDTO);
    }

    @Override
    public void updateDishState(int id,int userId,boolean state) {
        int restaurantId = dishPersistencePort.findDishById(id).getRestaurantId();
        boolean ownership = restaurantPersistencePort.getOwnership(restaurantId,userId);

        if(!ownership){
            throw new InvalidOwnerException();
        }

        dishPersistencePort.updateDishState(id,state);
    }

}
