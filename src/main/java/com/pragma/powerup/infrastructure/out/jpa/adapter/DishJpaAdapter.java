package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public void saveDish(DishModel dishModel) {
        dishRepository.save(dishEntityMapper.toEntity(dishModel));
    }

    @Override
    public void updateDish(int id, DishPartialUpdateDTO dishPartialUpdateDTO) {
        DishModel dishModel = dishEntityMapper.toDishModel(dishRepository.findById(id).orElseThrow(NoDataFoundException::new));
        if (dishModel != null) {
            if(dishPartialUpdateDTO.getDescription() != null){
                dishModel.setDescription(dishPartialUpdateDTO.getDescription());
            }
            if(dishPartialUpdateDTO.getPrice() != null){
                dishModel.setPrice(dishPartialUpdateDTO.getPrice());
            }
        }
        dishRepository.save(dishEntityMapper.toEntity(dishModel));
    }

    @Override
    public DishModel findDishById(int id){
        return dishEntityMapper.toDishModel(dishRepository.findById(id).orElseThrow(NoDataFoundException::new));
    }
}
