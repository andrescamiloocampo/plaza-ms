package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IDishEntityMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderDishEntityMapper {
    @Mapping(target = "dish", source = "dish")
    @Mapping(target = "quantity", source = "quantity")
    OrderDishModel toOrderDishModel(OrderDishEntity orderDishEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "dish", ignore = true)
    OrderDishEntity toOrderDishEntity(OrderDishModel orderDishModel);
}
