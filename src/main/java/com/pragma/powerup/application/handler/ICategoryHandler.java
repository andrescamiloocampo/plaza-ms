package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.response.CategoryResponseDto;

import java.util.List;

public interface ICategoryHandler {
    List<CategoryResponseDto> getAllCategories();
}
