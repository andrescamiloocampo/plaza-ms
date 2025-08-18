package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.InvalidNameException;
import com.pragma.powerup.domain.exception.InvalidNitException;
import com.pragma.powerup.domain.exception.InvalidPhoneException;
import com.pragma.powerup.domain.exception.InvalidRoleException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserAuthClientPort;
import com.pragma.powerup.infrastructure.out.feign.dto.response.RoleResponseDto;
import com.pragma.powerup.infrastructure.out.feign.dto.response.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RestaurantUseCaseTest {
    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserAuthClientPort userAuthClientPort;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    private RestaurantModel getValidRestaurant() {
        return new RestaurantModel(
                1,
                "Valid Restaurant",
                "123456789",
                5,
                "+573001234567",
                "Cra 123",
                "900123456"
        );
    }

    private UserResponseDto getOwnerUser() {
        RoleResponseDto role = new RoleResponseDto();
        role.setName("OWNER");

        UserResponseDto user = new UserResponseDto();
        user.setId(5);
        user.setName("Carlos");
        user.setLastname("Ramírez");
        user.setPhone("+573001112233");
        user.setRoles(List.of(role));
        return user;
    }

    @Test
    void saveRestaurant_ShouldSave_WhenValidData() {
        RestaurantModel restaurant = getValidRestaurant();
        when(userAuthClientPort.getUserById("5")).thenReturn(getOwnerUser());

        assertDoesNotThrow(() -> restaurantUseCase.saveRestaurant(restaurant));
        verify(restaurantPersistencePort).saveRestaurant(restaurant);
    }

    @Test
    void saveRestaurant_ShouldThrowInvalidRole_WhenUserIsNotOwner() {
        RestaurantModel restaurant = getValidRestaurant();
        UserResponseDto user = new UserResponseDto();
        RoleResponseDto role = new RoleResponseDto();
        role.setName("CLIENT");
        user.setRoles(List.of(role));

        when(userAuthClientPort.getUserById("5")).thenReturn(user);

        assertThrows(InvalidRoleException.class, () -> restaurantUseCase.saveRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void saveRestaurant_ShouldThrowInvalidNit_WhenNitIsNotNumeric() {
        RestaurantModel restaurant = getValidRestaurant();
        restaurant.setNit("ABC123XYZ");

        when(userAuthClientPort.getUserById("5")).thenReturn(getOwnerUser());

        assertThrows(InvalidNitException.class, () -> restaurantUseCase.saveRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void saveRestaurant_ShouldThrowInvalidPhone_WhenPhoneFormatIsIncorrect() {
        RestaurantModel restaurant = getValidRestaurant();
        restaurant.setPhone("abc-def-ghij");

        when(userAuthClientPort.getUserById("5")).thenReturn(getOwnerUser());

        assertThrows(InvalidPhoneException.class, () -> restaurantUseCase.saveRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void saveRestaurant_ShouldThrowInvalidName_WhenNameIsOnlyDigits() {
        RestaurantModel restaurant = getValidRestaurant();
        restaurant.setName("123456");

        when(userAuthClientPort.getUserById("5")).thenReturn(getOwnerUser());

        assertThrows(InvalidNameException.class, () -> restaurantUseCase.saveRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

}