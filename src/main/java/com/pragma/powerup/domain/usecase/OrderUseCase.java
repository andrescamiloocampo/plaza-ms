package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderState;
import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.spi.INotificationPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IUserAuthClientPort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final INotificationPort notificationPort;
    private final IUserAuthClientPort authClientPort;
    private static final Random random = new Random();

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
                        INotificationPort notificationPort,
                        IUserAuthClientPort authClientPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.notificationPort = notificationPort;
        this.authClientPort = authClientPort;
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
    public void assignOrder(int orderId, int employeeId) {
        RestaurantEmployeeModel employee = restaurantEmployeePersistencePort.findByUserId(employeeId);

        if (employee == null) {
            throw new UserNotFoundException();
        }

        OrderModel order = orderPersistencePort.getOrderById(orderId);

        if (!order.getState().equals(OrderState.PENDING.label)) {
            throw new OrderInProcessException();
        }

        if (order.getRestaurantId() != employee.getRestaurantId()) {
            throw new InvalidUserException();
        }

        order.setState(OrderState.PREPARATION.label);
        order.setChefId(employeeId);
        orderPersistencePort.updateOrder(order);
    }

    @Override
    public void completeOrder(int orderId, int employeeId) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);
        String userPhone = authClientPort.getUserById(String.valueOf(order.getUserId())).getPhone();

        if (order.getChefId() != employeeId) {
            throw new InvalidUserException();
        }

        if (Set.of(OrderState.DONE.label, OrderState.DELIVERED.label).contains(order.getState())) {
            throw new InvalidOrderActionException();
        }

        String securityPin = String.format("%04d", random.nextInt(10000));
        order.setState(OrderState.DONE.label);
        order.setPin(securityPin);
        orderPersistencePort.updateOrder(order);

        notificationPort.sendNotification(
                userPhone.trim(),
                "Your order is ready the security pin is: " + securityPin
        );
    }

    @Override
    public void deliverOrder(int userId, int orderId, String securityPin) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);
        if(order.getChefId() != userId){
            throw new InvalidUserException();
        }

        if(!OrderState.DONE.label.equals(order.getState())){
            throw new InvalidOrderActionException();
        }

        if (OrderState.DELIVERED.label.equals(order.getState())) {
            throw new InvalidOrderActionException();
        }

        if(!order.getPin().equals(securityPin)){
            throw new WrongCredentialsException();
        }

        order.setState(OrderState.DELIVERED.label);
        orderPersistencePort.updateOrder(order);
    }

    @Override
    public void cancelOrder(int customerId) {
        OrderModel currentOrder = orderPersistencePort.getOrderByUserId(customerId);

        if(currentOrder == null){
            throw new OrderNotFoundException();
        }

        if(!OrderState.PENDING.label.equals(currentOrder.getState())){
            throw new InvalidOrderActionException();
        }

        currentOrder.setState(OrderState.CANCELED.label);
        orderPersistencePort.updateOrder(currentOrder);
    }

    @Override
    public List<OrderModel> getOrders(int page, int size, String state, int employeeId) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException();
        }

        RestaurantEmployeeModel employee = restaurantEmployeePersistencePort.findByUserId(employeeId);

        if (employee == null) {
            throw new UserNotFoundException();
        }

        return orderPersistencePort.getOrders(employee.getRestaurantId(), page, size, state);
    }
}