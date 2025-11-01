package com.pragma.powerup.domain.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedDishModel {
    Integer totalPages;
    Long totalItems;
    List<DishModel> dishes;
}
