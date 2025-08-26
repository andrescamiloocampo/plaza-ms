package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity,Integer>, JpaSpecificationExecutor<RestaurantEntity> {
    boolean existsByIdAndOwnerId(int id, int ownerId);
    List<RestaurantEntity> findByOwnerId(int ownerId);
}
