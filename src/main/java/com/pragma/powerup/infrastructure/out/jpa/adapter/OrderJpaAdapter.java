package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.powerup.infrastructure.out.jpa.specification.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public void makeOrder(OrderModel orderModel) {
        orderRepository.save(orderEntityMapper.toEntityWithRelations(orderModel));
    }

    @Override
    public void updateOrder(OrderModel orderModel) {
        orderRepository.save(orderEntityMapper.toEntityWithRelations(orderModel));
    }

    @Override
    public OrderModel getOrderById(int id) {
        OrderModel foundOrder = orderEntityMapper.toModel(orderRepository.findById(id).orElse(null));
        if (foundOrder == null) {
            throw new NoDataFoundException();
        }
        return foundOrder;
    }

    @Override
    public OrderModel getOrderByUserId(int id) {
        return orderRepository
                .findTopByUserIdOrderByDateDesc(id)
                .map(orderEntityMapper::toModel)
                .orElse(null);
    }

    @Override
    public List<OrderModel> getOrders(int restaurantId, int page, int size, String state) {
        Pageable pageable = PageRequest.of(page,size);
        Specification<OrderEntity> spec = OrderSpecification.withFilters(restaurantId, state);
        return orderEntityMapper.toModelList(orderRepository.findAll(spec, pageable).getContent());
    }
}
