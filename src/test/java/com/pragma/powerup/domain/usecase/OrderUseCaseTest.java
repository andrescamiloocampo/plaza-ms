package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.InvalidUserException;
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

    private RestaurantEmployeeModel employee;
    private OrderModel order;

    @BeforeEach
    void setUp(){
        openMocks(this);
        employee = new RestaurantEmployeeModel();
        employee.setRestaurantId(1);
        order = new OrderModel();
        order.setId(100);
        order.setRestaurantId(1);
        order.setState(OrderState.PENDING.label);
    }

    @Test
    void makeOrder_shouldThrowException_whenLastOrderNotDelivered() {
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState(OrderState.PENDING.label);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);

        assertThrows(OrderInProcessException.class, ()->orderUseCase.makeOrder(order));
    }

    @Test
    void makeOrder_shouldCreateOrder_whenNoLastOrder() {
        order.setUserId(1);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(null);

        orderUseCase.makeOrder(order);

        assertNotNull(order.getDate());
        assertEquals(OrderState.PENDING.label, order.getState());
        verify(orderPersistencePort).makeOrder(order);
    }

    @Test
    void makeOrder_shouldCreateOrder_whenLastOrderDelivered() {
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
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState(null);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);

        assertThrows(NullPointerException.class,()->orderUseCase.makeOrder(order));
    }

    @Test
    void makeOrder_shouldFail_whenLastOrderStateIsEmpty() {
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
        employee.setRestaurantId(5);

        List<OrderModel> expectedOrders = List.of(new OrderModel(), new OrderModel());

        when(restaurantEmployeePersistencePort.findByUserId(1)).thenReturn(employee);
        when(orderPersistencePort.getOrders(5, 0, 10, "PENDING")).thenReturn(expectedOrders);

        List<OrderModel> result = orderUseCase.getOrders(0, 10, "PENDING", 1);

        assertEquals(2, result.size());
        verify(orderPersistencePort).getOrders(5, 0, 10, "PENDING");
    }

    @Test
    void testUpdateOrder_Success() {
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(employee);
        when(orderPersistencePort.getOrderById(100)).thenReturn(order);

        orderUseCase.updateOrder(100, 10);

        assertEquals("PREPARATION", order.getState());
        assertEquals(10, order.getChefId());
        verify(orderPersistencePort).updateOrder(order);
    }

    @Test
    void testUpdateOrder_EmployeeNotFound() {
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> orderUseCase.updateOrder(100, 10));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void testUpdateOrder_OrderNotPending() {
        order.setState("DELIVERED");
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(employee);
        when(orderPersistencePort.getOrderById(100)).thenReturn(order);

        assertThrows(OrderInProcessException.class, () -> orderUseCase.updateOrder(100, 10));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void testUpdateOrder_InvalidRestaurant() {
        employee.setRestaurantId(2);
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(employee);
        when(orderPersistencePort.getOrderById(100)).thenReturn(order);

        assertThrows(InvalidUserException.class, () -> orderUseCase.updateOrder(100, 10));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

}