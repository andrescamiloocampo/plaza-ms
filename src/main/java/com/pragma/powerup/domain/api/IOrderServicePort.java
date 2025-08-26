package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderModel;

import java.util.List;

public interface IOrderServicePort {
    void makeOrder(OrderModel order);
    void updateOrder(int orderId, int employeeId);
    List<OrderModel> getOrders(int page, int size, String state,int employeeId);
}
