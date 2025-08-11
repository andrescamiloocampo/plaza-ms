package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity,Integer> {
    boolean existsByIdAndOwnerId(int id, int ownerId);
}
