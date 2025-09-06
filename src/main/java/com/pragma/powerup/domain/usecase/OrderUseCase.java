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

    private final Set<String> allowedOrderStatesForNewOrder = Set.of(
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

        if (lastUserOrder != null && !allowedOrderStatesForNewOrder.contains(lastUserOrder.getState())) {
            throw new OrderInProcessException();
        }

        createAndSaveOrder(order);
    }

    @Override
    public void assignOrder(int orderId, int employeeId) {
        RestaurantEmployeeModel employee = restaurantEmployeePersistencePort.findByUserId(employeeId);
        validateEmployee(employee);

        OrderModel order = orderPersistencePort.getOrderById(orderId);
        validateOrderStateAndRestaurant(order, employee.getRestaurantId());

        OrderLogStatus orderLogStatus = new OrderLogStatus(order.getState(), OrderState.PREPARATION.label, LocalDateTime.now());

        order.setState(OrderState.PREPARATION.label);
        order.setChefId(employeeId);

        orderPersistencePort.updateOrder(order);
        logOrderStatusChange(order.getId(), order.getRestaurantId(), employeeId, order.getUserId(), orderLogStatus);
    }

    @Override
    public void completeOrder(int orderId, int employeeId) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);

        validateOrderCompletion(order, employeeId);

        OrderLogStatus orderLogStatus = new OrderLogStatus(order.getState(), OrderState.DONE.label, LocalDateTime.now());

        String securityPin = generateAndSetPin(order);
        orderPersistencePort.updateOrder(order);

        sendNotificationToUser(order.getUserId(), securityPin);

        logOrderStatusChange(order.getId(), order.getRestaurantId(), employeeId, order.getUserId(), orderLogStatus);
    }

    @Override
    public void deliverOrder(int userId, int orderId, String securityPin) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);

        validateOrderDelivery(order, userId, securityPin);
        OrderLogStatus orderLogStatus = new OrderLogStatus(order.getState(), OrderState.DELIVERED.label, LocalDateTime.now());

        order.setState(OrderState.DELIVERED.label);

        orderPersistencePort.updateOrder(order);

        OrderLogModel orderLogModel = new OrderLogModel((long) order.getId(), (long) userId, (long) order.getUserId(), (long) order.getRestaurantId(), List.of(orderLogStatus));
        orderLogsClientPort.logOrderStatusChange(orderLogModel);
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

        OrderLogStatus orderLogStatus = new OrderLogStatus(currentOrder.getState(), OrderState.CANCELED.label, LocalDateTime.now());
        currentOrder.setState(OrderState.CANCELED.label);
        orderPersistencePort.updateOrder(currentOrder);
        logOrderStatusChange(currentOrder.getId(), currentOrder.getRestaurantId(), 0, customerId, orderLogStatus);
    }

    @Override
    public List<OrderModel> getOrders(int page, int size, String state, int employeeId) {
        validatePaginationParameters(page, size);

        RestaurantEmployeeModel employee = restaurantEmployeePersistencePort.findByUserId(employeeId);
        validateEmployee(employee);

        return orderPersistencePort.getOrders(employee.getRestaurantId(), page, size, state);
    }


    private void createAndSaveOrder(OrderModel order) {
        order.setDate(LocalDateTime.now());
        order.setState(OrderState.PENDING.label);
        OrderModel savedOrder = orderPersistencePort.makeOrder(order);
        logOrderStatusChange(savedOrder.getId(), order.getRestaurantId(), 0, order.getUserId(), new OrderLogStatus(null, OrderState.PENDING.label, LocalDateTime.now()));
    }

    private void validatePaginationParameters(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size parameters.");
        }
    }

    private void validateEmployee(RestaurantEmployeeModel employee) {
        if (employee == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateOrderStateAndRestaurant(OrderModel order, int employeeRestaurantId) {
        if (order == null) {
            throw new OrderNotFoundException();
        }
        if (!OrderState.PENDING.label.equals(order.getState())) {
            throw new OrderInProcessException();
        }
        if (order.getRestaurantId() != employeeRestaurantId) {
            throw new InvalidUserException();
        }
    }

    private void validateOrderCompletion(OrderModel order, int employeeId) {
        if (order == null) {
            throw new OrderNotFoundException();
        }
        if (order.getChefId() != employeeId) {
            throw new InvalidUserException();
        }
        Set<String> invalidStates = Set.of(OrderState.DONE.label, OrderState.DELIVERED.label);
        if (invalidStates.contains(order.getState())) {
            throw new InvalidOrderActionException();
        }
    }

    private void validateOrderDelivery(OrderModel order, int userId, String securityPin) {
        if (order == null) {
            throw new OrderNotFoundException();
        }
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
    }

    private String generateAndSetPin(OrderModel order) {
        String securityPin = String.format("%04d", random.nextInt(10000));
        order.setState(OrderState.DONE.label);
        order.setPin(securityPin);
        return securityPin;
    }

    private void sendNotificationToUser(int userId, String securityPin) {
        String userPhone = authClientPort.getUserById(String.valueOf(userId)).getPhone();
        notificationPort.sendNotification(
                userPhone.trim(),
                "Your order is ready the security pin is: " + securityPin
        );
    }

    private void logOrderStatusChange(long orderId, Integer restaurantId, Integer employeeId, long userId, OrderLogStatus status) {
        OrderLogModel orderLogModel = new OrderLogModel(orderId, (long) employeeId, userId, (long) restaurantId, List.of(status));
        orderLogsClientPort.logOrderStatusChange(orderLogModel);
    }
}