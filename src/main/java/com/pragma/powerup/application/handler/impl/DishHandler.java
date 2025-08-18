package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.domain.api.IDishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final IDishServicePort dishServicePort;
    private final IDishRequestMapper dishRequestMapper;

    @Override
    public void saveDish(DishRequestDto dishRequestDto, int userId) {
        dishServicePort.saveDish(dishRequestMapper.toDish(dishRequestDto),userId);
    }

    @Override
    public void updateDish(int id,int userId, DishPartialUpdateDTO dishPartialUpdateDTO){
        dishServicePort.updateDish(id,userId, dishPartialUpdateDTO);
    }

    @Override
    public void updateDishState(int id, int userId, boolean state) {
     dishServicePort.updateDishState(id,userId,state);
    }
}
