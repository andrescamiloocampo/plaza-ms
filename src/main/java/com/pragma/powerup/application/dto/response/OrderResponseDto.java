package com.pragma.powerup.application.dto.response;

import com.pragma.powerup.domain.model.OrderDishModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDto {
    private int id;
    private int userId;
    private LocalDateTime date;
    private String state;
    private int chefId;
    private int restaurantId;
    private String pin;
    private List<OrderDishModel> orderDishes;
}
