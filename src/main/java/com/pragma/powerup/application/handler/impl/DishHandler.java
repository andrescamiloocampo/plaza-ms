package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDto;
import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public void saveDish(DishRequestDto dishRequestDto, int userId) {
        dishServicePort.saveDish(dishRequestMapper.toDish(dishRequestDto), userId);
    }

    @Override
    public void updateDish(int id, int userId, DishPartialUpdateDto dishPartialUpdateDTO) {
        dishServicePort.updateDish(id, userId, dishPartialUpdateDTO);
    }

    @Override
    public void updateDishState(int id, int userId, boolean state) {
        dishServicePort.updateDishState(id, userId, state);
    }

    @Override
    public List<DishResponseDto> getDishes(int restaurantId, int page, int size, String category) {
        return dishResponseMapper.toResponseList(dishServicePort.getDishes(restaurantId, page, size, category));
    }
}
