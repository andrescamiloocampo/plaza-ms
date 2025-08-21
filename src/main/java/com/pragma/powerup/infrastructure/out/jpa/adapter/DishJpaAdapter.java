package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.application.dto.request.DishPartialUpdateDTO;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import com.pragma.powerup.infrastructure.out.jpa.specification.DishSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Locale;

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
            if (dishPartialUpdateDTO.getDescription() != null) {
                dishModel.setDescription(dishPartialUpdateDTO.getDescription());
            }
            if (dishPartialUpdateDTO.getPrice() != null) {
                dishModel.setPrice(dishPartialUpdateDTO.getPrice());
            }
        }
        dishRepository.save(dishEntityMapper.toEntity(dishModel));
    }

    @Override
    public void updateDishState(int id, boolean state) {
        DishModel dishModel = dishEntityMapper.toDishModel(dishRepository.findById(id).orElseThrow(NoDataFoundException::new));
        dishModel.setActive(state);
        dishRepository.save(dishEntityMapper.toEntity(dishModel));
    }

    @Override
    public DishModel findDishById(int id) {
        return dishEntityMapper.toDishModel(dishRepository.findById(id).orElseThrow(NoDataFoundException::new));
    }

    @Override
    public List<DishModel> getDishes(int restaurantId, int page, int size, String category) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<DishEntity> spec = DishSpecification.withFilters(restaurantId, category);
        return dishEntityMapper.toDishModelList(dishRepository.findAll(spec, pageable).getContent());
    }
}
