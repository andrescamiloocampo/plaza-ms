package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.CategoryModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ICategoryEntityMapper {
    CategoryEntity toEntity(CategoryModel categoryModel);
    List<CategoryEntity> toEntityList(List<CategoryModel> categoryModelList);
    CategoryModel toCategoryModel(CategoryEntity categoryEntity);
    List<CategoryModel> toCategoryModelList(List<CategoryEntity> categoryEntityList);
}
