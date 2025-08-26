package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderModel;

import java.util.List;

public interface IOrderPersistencePort {
    void makeOrder(OrderModel orderModel);

    OrderModel getOrderByUserId(int id);

    List<OrderModel> getOrders(int restaurantId, int page, int size, String state);
}
