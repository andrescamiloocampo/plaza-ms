package com.pragma.powerup.application.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedDishResponseDto {
    Integer totalPages;
    Long totalItems;
    List<DishResponseDto> dishes;
}
