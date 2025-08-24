package com.pragma.powerup.application.mapper;

import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IOrderDishRequestMapper {
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "userId", ignore = true)
    OrderDishEntity toEntity(OrderDishModel orderDishModel);

    OrderDishModel toModel(OrderDishEntity orderDishEntity);
}
