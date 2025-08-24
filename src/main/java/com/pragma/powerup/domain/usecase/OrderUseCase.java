package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.OrderInProcessException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderState;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;

import java.time.LocalDateTime;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort){
        this.orderPersistencePort = orderPersistencePort;
    }

    @Override
    public void makeOrder(OrderModel order){
        OrderModel lastUserOrder = orderPersistencePort.getOrderByUserId(order.getUserId());

        if(lastUserOrder != null && !lastUserOrder.getState().equals(OrderState.DELIVERED.label)){
            throw new OrderInProcessException();
        }
        
        order.setDate(LocalDateTime.now());
        order.setState(OrderState.PENDING.label);
        orderPersistencePort.makeOrder(order);
    }
}
