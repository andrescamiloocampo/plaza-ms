package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;

import java.util.List;

public interface IOrderHandler {
    void makeOrder(OrderRequestDto orderRequestDto, int userId);
    void updateOrder(int orderId, int employeeId);
    void notifyOrderReady(int orderId, int userId);
    List<OrderResponseDto> getOrders(int page, int size, String state,int userId);
}
