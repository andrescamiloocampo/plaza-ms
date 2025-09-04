package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderModel;

import java.util.List;

public interface IOrderPersistencePort {
    OrderModel makeOrder(OrderModel orderModel);

    void updateOrder(OrderModel orderModel);

    OrderModel getOrderById(int id);

    OrderModel getOrderByUserId(int id);

    List<OrderModel> getOrders(int restaurantId, int page, int size, String state);
}
