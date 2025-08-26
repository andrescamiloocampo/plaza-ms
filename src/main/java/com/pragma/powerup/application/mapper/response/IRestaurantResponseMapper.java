package com.pragma.powerup.application.mapper.response;

import com.pragma.powerup.application.dto.response.RestaurantPartialResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.domain.model.RestaurantModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface IRestaurantResponseMapper {
    RestaurantResponseDto toResponse(RestaurantModel restaurantModel);
    List<RestaurantResponseDto> toResponseList(List<RestaurantModel> restaurantModelList);
    RestaurantPartialResponseDto toPartialResponse(RestaurantModel restaurantModel);
    List<RestaurantPartialResponseDto> toPartialResponseList(List<RestaurantModel> restaurantModelList);
}
