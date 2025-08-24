package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IOrderServicePort;
import com.pragma.powerup.domain.exception.OrderInProcessException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderState;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.Mockito.*;

class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @InjectMocks
    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp(){
        openMocks(this);
    }

    @Test
    void makeOrder_shouldThrowException_whenLastOrderNotDelivered() {
        OrderModel order = new OrderModel();
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState(OrderState.PENDING.label);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);

        assertThrows(OrderInProcessException.class, ()->orderUseCase.makeOrder(order));
    }

    @Test
    void makeOrder_shouldCreateOrder_whenNoLastOrder() {
        OrderModel order = new OrderModel();
        order.setUserId(1);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(null);

        orderUseCase.makeOrder(order);

        assertNotNull(order.getDate());
        assertEquals(OrderState.PENDING.label, order.getState());
        verify(orderPersistencePort).makeOrder(order);
    }

    @Test
    void makeOrder_shouldCreateOrder_whenLastOrderDelivered() {
        OrderModel order = new OrderModel();
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState(OrderState.DELIVERED.label);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);
        orderUseCase.makeOrder(order);

        assertEquals(OrderState.PENDING.label, order.getState());
        verify(orderPersistencePort).makeOrder(order);
    }

    @Test
    void makeOrder_shouldFail_whenLastOrderStateIsNull() {
        OrderModel order = new OrderModel();
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState(null);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);

        assertThrows(NullPointerException.class,()->orderUseCase.makeOrder(order));
    }

    @Test
    void makeOrder_shouldFail_whenLastOrderStateIsEmpty() {
        OrderModel order = new OrderModel();
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState("");

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);

        assertThrows(OrderInProcessException.class, ()->orderUseCase.makeOrder(order));
    }

}