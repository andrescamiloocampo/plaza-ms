package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.OrderDishDto;
import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IOrderDishRequestMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IOrderRequestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "date", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", constant = "PENDIENTE")
    @Mapping(target = "chefId", ignore = true)
    @Mapping(target = "restaurantId", source = "orderRequestDto.restaurantId")
    @Mapping(target = "orderDishes", source = "orderRequestDto.dishes")
    OrderModel toModel(OrderRequestDto orderRequestDto, int userId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dish", source = "dishId", qualifiedByName = "createDishWithId")
    @Mapping(target = "quantity", source = "quantity")
    OrderDishModel toOrderDishModel(OrderDishDto orderDishDto);

    @Named("createDishWithId")
    default DishModel createDishWithId(int dishId) {
        DishModel dish = new DishModel();
        dish.setId(dishId);
        return dish;
    }
}
