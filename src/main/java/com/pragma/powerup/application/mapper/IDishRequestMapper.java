package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.domain.model.CategoryModel;
import com.pragma.powerup.domain.model.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishRequestMapper {
    @Mapping(target = "category", expression = "java(mapCategory(dishRequestDto.getCategoryId()))")
    DishModel toDish(DishRequestDto dishRequestDto);

    default CategoryModel mapCategory(int categoryId){
        if(categoryId == 0) return null;
        CategoryModel category = new CategoryModel();
        category.setId(categoryId);
        return category;
    }
}
