package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.OrderInProcessException;
import com.pragma.powerup.domain.exception.UserNotFoundException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderState;
import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.Mockito.*;

class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

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

    @Test
    void getOrders_shouldThrowException_whenPageOrSizeInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                orderUseCase.getOrders(-1, 10, "PENDING", 1));

        assertThrows(IllegalArgumentException.class, () ->
                orderUseCase.getOrders(0, 0, "PENDING", 1));
    }

    @Test
    void getOrders_shouldThrowException_whenEmployeeNotFound() {
        when(restaurantEmployeePersistencePort.findByUserId(99)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () ->
                orderUseCase.getOrders(0, 10, "PENDING", 99));
    }

    @Test
    void getOrders_shouldReturnOrders_whenValidEmployee() {
        RestaurantEmployeeModel employee = new RestaurantEmployeeModel();
        employee.setRestaurantId(5);

        List<OrderModel> expectedOrders = List.of(new OrderModel(), new OrderModel());

        when(restaurantEmployeePersistencePort.findByUserId(1)).thenReturn(employee);
        when(orderPersistencePort.getOrders(5, 0, 10, "PENDING")).thenReturn(expectedOrders);

        List<OrderModel> result = orderUseCase.getOrders(0, 10, "PENDING", 1);

        assertEquals(2, result.size());
        verify(orderPersistencePort).getOrders(5, 0, 10, "PENDING");
    }

}