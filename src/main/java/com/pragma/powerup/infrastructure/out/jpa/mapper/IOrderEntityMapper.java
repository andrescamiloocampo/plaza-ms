package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {java.util.stream.Collectors.class}
)
public interface IOrderEntityMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "chefId", source = "chefId")
    @Mapping(target = "restaurant", source = "restaurantId", qualifiedByName = "createRestaurantWithId")
    @Mapping(target = "orderDishes", ignore = true)
    OrderEntity toEntity(OrderModel orderModel);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "chefId", source = "chefId")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "orderDishes", source = "orderDishes")
    OrderModel toModel(OrderEntity orderEntity);

//    @Mapping(target = "id", source = "id")
//    @Mapping(target = "userId", source = "userId")
//    @Mapping(target = "date", source = "date")
//    @Mapping(target = "state", source = "state")
//    @Mapping(target = "chefId", source = "chefId")
//    @Mapping(target = "restaurantId", source = "restaurant.id")
//    @Mapping(target = "orderDishes", source = "orderDishes")
//    List<OrderModel> toModelList(List<OrderEntity> orderEntities);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "orderDishes", source = "orderDishes", qualifiedByName = "toOrderDishModelList")
    List<OrderModel> toModelList(List<OrderEntity> orderEntities);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "dish", source = "dish", qualifiedByName = "createDishEntityWithId")
    @Mapping(target = "quantity", source = "quantity")
    OrderDishEntity toOrderDishEntity(OrderDishModel orderDishModel);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "dish", source = "dish")
    @Mapping(target = "quantity", source = "quantity")
    OrderDishModel toOrderDishModel(OrderDishEntity orderDishEntity);

    @Named("createRestaurantWithId")
    default RestaurantEntity createRestaurantWithId(int restaurantId) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        return restaurant;
    }

    @Named("createDishEntityWithId")
    default DishEntity createDishEntityWithId(DishModel dishModel) {
        if (dishModel == null) return null;
        DishEntity dish = new DishEntity();
        dish.setId(dishModel.getId());

        return dish;
    }

    @Named("toOrderDishEntityList")
    default List<OrderDishEntity> toOrderDishEntityList(List<OrderDishModel> orderDishModels){
        if(orderDishModels == null) return null;
        return orderDishModels.stream()
                .map(this::toOrderDishEntity)
                .collect(Collectors.toList());
    }

    @Named("toOrderDishModelList")
    default List<OrderDishModel> toOrderDishModelList(List<OrderDishEntity> orderDishEntities){
        if(orderDishEntities == null) return null;
        return orderDishEntities.stream()
                .map(this::toOrderDishModel)
                .collect(Collectors.toList());
    }

    default OrderEntity toEntityWithRelations(OrderModel orderModel) {
        OrderEntity orderEntity = toEntity(orderModel);

        if (orderModel.getOrderDishes() != null) {
            List<OrderDishEntity> orderDishEntities = orderModel.getOrderDishes().stream()
                    .map(orderDishModel -> {
                        OrderDishEntity orderDishEntity = toOrderDishEntity(orderDishModel);
                        orderDishEntity.setOrder(orderEntity);
                        orderDishEntity.setUserId(orderEntity.getUserId());
                        return orderDishEntity;
                    })
                    .collect(Collectors.toList());

            orderEntity.setOrderDishes(orderDishEntities);
        }

        return orderEntity;
    }
}