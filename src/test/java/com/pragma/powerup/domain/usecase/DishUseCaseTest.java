package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.InvalidCategoryException;
import com.pragma.powerup.domain.exception.InvalidOwnerException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class DishUseCaseTest {
    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private ICategoryPersistencePort categoryPersistencePort;

    @InjectMocks
    private DishUseCase dishUseCase;
    private DishModel dishModel;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        dishModel = new DishModel();
        dishModel.setRestaurantId(1);
        dishModel.setCategoryId(2);
    }

    @Test
    void saveDish_success() {
        when(restaurantPersistencePort.getOwnership(1, 99)).thenReturn(true);
        when(categoryPersistencePort.existsById(2)).thenReturn(true);

        dishUseCase.saveDish(dishModel, 99);

        verify(dishPersistencePort).saveDish(dishModel);
    }

    @Test
    void saveDish_invalidOwner() {
        when(restaurantPersistencePort.getOwnership(1, 99)).thenReturn(false);

        assertThrows(InvalidOwnerException.class, () -> dishUseCase.saveDish(dishModel, 99));
        verify(dishPersistencePort, never()).saveDish(any());
    }

    @Test
    void saveDish_invalidCategory() {
        when(restaurantPersistencePort.getOwnership(1, 99)).thenReturn(true);
        when(categoryPersistencePort.existsById(2)).thenReturn(false);

        assertThrows(InvalidCategoryException.class, () -> dishUseCase.saveDish(dishModel, 99));
        verify(dishPersistencePort,never()).saveDish(any());
    }
}