package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OrderUseCase implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final INotificationPort notificationPort;
    private final IUserAuthClientPort authClientPort;
    private final IOrderLogsClientPort orderLogsClientPort;
    private static final Random random = new Random();

    private final Set<String> allowedOrderStates = Set.of(
            OrderState.DELIVERED.label,
            OrderState.CANCELED.label
    );

    public OrderUseCase(IOrderPersistencePort orderPersistencePort,
                        IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
                        INotificationPort notificationPort,
                        IUserAuthClientPort authClientPort,
                        IOrderLogsClientPort orderLogsClientPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.notificationPort = notificationPort;
        this.authClientPort = authClientPort;
        this.orderLogsClientPort = orderLogsClientPort;
    }

    @Override
    public void makeOrder(OrderModel order) {
        OrderModel lastUserOrder = orderPersistencePort.getOrderByUserId(order.getUserId());

        if (lastUserOrder != null && !this.allowedOrderStates.contains(lastUserOrder.getState())) {
            throw new OrderInProcessException();
        }

        order.setDate(LocalDateTime.now());
        order.setState(OrderState.PENDING.label);
        OrderModel savedOrder = orderPersistencePort.makeOrder(order);
        OrderLogStatus orderLogStatus = new OrderLogStatus(null, OrderState.PENDING.label, LocalDateTime.now());
        OrderLogModel orderLogModel = new OrderLogModel((long) savedOrder.getId(), null, (long) order.getUserId(), List.of(orderLogStatus));
        orderLogsClientPort.logOrderStatusChange(orderLogModel);
    }

    @Override
    public void assignOrder(int orderId, int employeeId) {
        RestaurantEmployeeModel employee = restaurantEmployeePersistencePort.findByUserId(employeeId);
        validateEmployee(employee);

        OrderModel order = orderPersistencePort.getOrderById(orderId);
        validateOrderState(order);
        validateSameRestaurant(order, employee);

        OrderLogStatus orderLogStatus = new OrderLogStatus();
        orderLogStatus.setPreviousState(order.getState());

        order.setState(OrderState.PREPARATION.label);
        order.setChefId(employeeId);

        orderLogStatus.setNewState(order.getState());
        orderLogStatus.setChangedAt(LocalDateTime.now());

        orderPersistencePort.updateOrder(order);
        OrderLogModel orderLogModel = new OrderLogModel((long) order.getId(), (long) employeeId, (long) order.getUserId(), List.of(orderLogStatus));
        orderLogsClientPort.logOrderStatusChange(orderLogModel);
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
        if (order.getChefId() != userId) {
            throw new InvalidUserException();
        }

        if (!OrderState.DONE.label.equals(order.getState())) {
            throw new InvalidOrderActionException();
        }

        if (OrderState.DELIVERED.label.equals(order.getState())) {
            throw new InvalidOrderActionException();
        }

        if (!order.getPin().equals(securityPin)) {
            throw new WrongCredentialsException();
        }

        order.setState(OrderState.DELIVERED.label);
        orderPersistencePort.updateOrder(order);
    }

    @Override
    public void cancelOrder(int customerId) {
        OrderModel currentOrder = orderPersistencePort.getOrderByUserId(customerId);

        if (currentOrder == null) {
            throw new OrderNotFoundException();
        }

        if (!OrderState.PENDING.label.equals(currentOrder.getState())) {
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

    private void validateEmployee(RestaurantEmployeeModel employee) {
        if (employee == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateOrderState(OrderModel order) {
        if (!OrderState.PENDING.label.equals(order.getState())) {
            throw new OrderInProcessException();
        }
    }

    private void validateSameRestaurant(OrderModel order, RestaurantEmployeeModel employee) {
        if (order.getRestaurantId() != employee.getRestaurantId()) {
            throw new InvalidUserException();
        }
    }

}