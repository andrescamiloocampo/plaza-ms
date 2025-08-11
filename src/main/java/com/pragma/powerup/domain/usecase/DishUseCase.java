package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDishServicePort;
import com.pragma.powerup.domain.exception.InvalidCategoryException;
import com.pragma.powerup.domain.exception.InvalidOwnerException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

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
    public void saveDish(DishModel dishModel, int userId) {
        boolean ownership = restaurantPersistencePort.getOwnership(dishModel.getRestaurantId(), userId);
        boolean isValidCategory = categoryPersistencePort.existsById(dishModel.getCategoryId());

        if (!ownership) {
            throw new InvalidOwnerException();
        }

        if(!isValidCategory){
            throw new InvalidCategoryException();
        }

        dishPersistencePort.saveDish(dishModel);
    }

}
