package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;


@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public void saveRestaurant(RestaurantModel restaurantModel) {
        RestaurantEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
        restaurantEntityMapper.toRestaurantModel(restaurantEntity);
    }

    @Override
    public RestaurantModel getRestaurantById(int id){
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id).orElseThrow(NoDataFoundException::new);
        return restaurantEntityMapper.toRestaurantModel(restaurantEntity);
    }

    @Override
    public List<RestaurantModel> getRestaurantsByOwnerId(int ownerId) {
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findByOwnerId(ownerId);
        return restaurantEntityMapper.toRestaurantModelList(restaurantEntities);
    }

    @Override
    public List<RestaurantModel> getRestaurants(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC,"name");
        Pageable pageable = PageRequest.of(page,size,sort);
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll(pageable).getContent();
        return restaurantEntityMapper.toRestaurantModelList(restaurantEntities);
    }

    @Override
    public boolean getOwnership(int id, int ownerId) {
        return restaurantRepository.existsByIdAndOwnerId(id,ownerId);
    }
}
