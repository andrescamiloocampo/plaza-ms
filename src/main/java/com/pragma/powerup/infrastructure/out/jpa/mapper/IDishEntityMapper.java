package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.CategoryEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IDishEntityMapper {

    @Mapping(target = "category", expression = "java(mapCategory(dishModel.getCategoryId()))")
    @Mapping(target = "restaurant", expression = "java(mapRestaurant(dishModel.getRestaurantId()))")
    DishEntity toEntity(DishModel dishModel);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    DishModel toDishModel(DishEntity dishEntity);

    List<DishModel> toDishModelList(List<DishEntity> dishEntities);

    default CategoryEntity mapCategory(int categoryId) {
        if (categoryId == 0) return null;
        CategoryEntity category = new CategoryEntity();
        category.setId(categoryId);
        return category;
    }

    default RestaurantEntity mapRestaurant(int restaurantId) {
        if (restaurantId == 0) return null;
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        return restaurant;
    }
}
