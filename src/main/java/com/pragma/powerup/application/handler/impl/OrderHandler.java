package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.mapper.request.IOrderRequestMapper;
import com.pragma.powerup.application.mapper.response.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IOrderServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public void makeOrder(OrderRequestDto orderRequestDto, int userId) {
        orderServicePort.makeOrder(orderRequestMapper.toModel(orderRequestDto, userId));
    }

    @Override
    public List<OrderResponseDto> getOrders(int page, int size, String state, int userId) {
        return orderResponseMapper
                .toResponseList(orderServicePort.getOrders(page, size, state.toUpperCase(), userId));
    }

}
