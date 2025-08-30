package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderModel;

import java.util.List;

public interface IOrderServicePort {
    void makeOrder(OrderModel order);
    void assignOrder(int orderId, int employeeId);
    void completeOrder(int orderId, int employeeId);
    void deliverOrder(int userId,int orderId, String securityPin);
    void cancelOrder(int customerId);
    List<OrderModel> getOrders(int page, int size, String state,int employeeId);
}
