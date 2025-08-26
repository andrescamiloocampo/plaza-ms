package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.awt.print.Pageable;
import java.util.Optional;

public interface IOrderRepository extends JpaRepository<OrderEntity,Integer>, JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findTopByUserIdOrderByDateDesc(int id);
}
