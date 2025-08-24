package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public void makeOrder(OrderModel orderModel) {
        orderRepository.save(orderEntityMapper.toEntityWithRelations(orderModel));
    }

    @Override
    public OrderModel getOrderByUserId(int id) {
        return orderRepository
                .findTopByUserIdOrderByDateDesc(id)
                .map(orderEntityMapper::toModel)
                .orElse(null);
    }
}
