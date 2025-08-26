package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.OrderInProcessException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderState;
import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;

import java.time.LocalDateTime;
import java.util.List;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
    }

    @Override
    public void makeOrder(OrderModel order) {
        OrderModel lastUserOrder = orderPersistencePort.getOrderByUserId(order.getUserId());

        if (lastUserOrder != null && !lastUserOrder.getState().equals(OrderState.DELIVERED.label)) {
            throw new OrderInProcessException();
        }

        order.setDate(LocalDateTime.now());
        order.setState(OrderState.PENDING.label);
        orderPersistencePort.makeOrder(order);
    }

    @Override
    public List<OrderModel> getOrders(int page, int size, String state,int employeeId) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        RestaurantEmployeeModel employee = restaurantEmployeePersistencePort.findByUserId(employeeId);

        if(employee == null){
            throw new UserNotFoundException();
        }

        return orderPersistencePort.getOrders(employee.getRestaurantId(),page, size, state);
    }
}
