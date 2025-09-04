package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.INotificationPort;
import com.pragma.powerup.domain.spi.IOrderLogsClientPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IUserAuthClientPort;
import com.pragma.powerup.infrastructure.out.feign.dto.response.RoleResponseDto;
import com.pragma.powerup.infrastructure.out.feign.dto.response.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.mockito.Mockito.*;

class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    @Mock
    private INotificationPort notificationPort;

    @Mock
    private IUserAuthClientPort userAuthClientPort;

    @Mock
    private IOrderLogsClientPort orderLogsClientPort;

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
        order.setUserId(10);
        order.setRestaurantId(1);
        order.setState(OrderState.PENDING.label);
    }

    public static UserResponseDto createUser(
            int id,
            String name,
            String lastname,
            String phone,
            LocalDate birthdate,
            List<RoleResponseDto> roles
    ) {
        UserResponseDto user = new UserResponseDto();
        user.setId(id);
        user.setName(name);
        user.setLastname(lastname);
        user.setPhone(phone);
        user.setBirthdate(birthdate);
        user.setRoles(roles);
        return user;
    }

    public static UserResponseDto createDefaultUser() {
        RoleResponseDto role = new RoleResponseDto();
        role.setName("CUSTOMER");
        role.setDescription("Grant customer role");

        return createUser(
                10,
                "Andres",
                "Ocampo",
                "  3216549870  ",
                LocalDate.of(1995, 3, 15),
                List.of(role)
        );
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
        when(orderPersistencePort.makeOrder(any(OrderModel.class))).thenReturn(order);

        orderUseCase.makeOrder(order);

        assertNotNull(order.getDate());
        assertEquals(OrderState.PENDING.label, order.getState());
        verify(orderPersistencePort).makeOrder(order);
        verify(orderLogsClientPort).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void makeOrder_shouldCreateOrder_whenLastOrderDelivered() {
        order.setUserId(1);

        OrderModel lastOrder = new OrderModel();
        lastOrder.setState(OrderState.DELIVERED.label);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(lastOrder);
        when(orderPersistencePort.makeOrder(any(OrderModel.class))).thenReturn(order);

        orderUseCase.makeOrder(order);

        assertEquals(OrderState.PENDING.label, order.getState());
        verify(orderPersistencePort).makeOrder(order);
        verify(orderLogsClientPort).logOrderStatusChange(any(OrderLogModel.class));
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

        orderUseCase.assignOrder(100, 10);

        assertEquals("PREPARATION", order.getState());
        assertEquals(10, order.getChefId());
        verify(orderPersistencePort).updateOrder(order);
        verify(orderLogsClientPort).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void testUpdateOrder_EmployeeNotFound() {
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> orderUseCase.assignOrder(100, 10));

        verify(orderPersistencePort, never()).updateOrder(any());
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void testUpdateOrder_OrderNotPending() {
        order.setState("DELIVERED");
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(employee);
        when(orderPersistencePort.getOrderById(100)).thenReturn(order);

        assertThrows(OrderInProcessException.class, () -> orderUseCase.assignOrder(100, 10));

        verify(orderPersistencePort, never()).updateOrder(any());
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void testUpdateOrder_InvalidRestaurant() {
        employee.setRestaurantId(2);
        when(restaurantEmployeePersistencePort.findByUserId(10)).thenReturn(employee);
        when(orderPersistencePort.getOrderById(100)).thenReturn(order);

        assertThrows(InvalidUserException.class, () -> orderUseCase.assignOrder(100, 10));

        verify(orderPersistencePort, never()).updateOrder(any());
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void completeOrder_shouldSendNotification_AndUpdateOrder() {
        order.setId(1);
        order.setUserId(10);
        order.setChefId(5);
        order.setState(OrderState.PENDING.label);

        when(orderPersistencePort.getOrderById(1)).thenReturn(order);
        when(userAuthClientPort.getUserById("10")).thenReturn(createDefaultUser());

        orderUseCase.completeOrder(1, 5);

        assertEquals(OrderState.DONE.label, order.getState());
        assertNotNull(order.getPin());
        assertFalse(order.getPin().isBlank());
        verify(orderPersistencePort).updateOrder(order);
        verify(notificationPort).sendNotification(
                "3216549870",
                "Your order is ready the security pin is: " + order.getPin()
        );
        verify(orderLogsClientPort).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void completeOrder_invalidChef_throwsException() {
        order.setChefId(99);
        order.setId(1);
        order.setUserId(10);
        when(orderPersistencePort.getOrderById(1)).thenReturn(order);
        when(userAuthClientPort.getUserById(""+order.getUserId())).thenReturn(createDefaultUser());

        assertThrows(InvalidUserException.class, () -> orderUseCase.completeOrder(1, 5));
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void completeOrder_alreadyDone_throwsException() {
        order.setChefId(5);
        order.setState(OrderState.DONE.label);
        order.setId(1);
        order.setUserId(10);
        when(orderPersistencePort.getOrderById(1)).thenReturn(order);
        when(userAuthClientPort.getUserById(""+order.getUserId())).thenReturn(createDefaultUser());

        assertThrows(InvalidOrderActionException.class, () -> orderUseCase.completeOrder(1, 5));
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void shouldThrowInvalidUserExceptionWhenChefIsDifferent() {
        order.setChefId(99);
        order.setState(OrderState.DONE.label);
        order.setPin("1234");

        when(orderPersistencePort.getOrderById(1)).thenReturn(order);

        assertThrows(InvalidUserException.class,
                () -> orderUseCase.deliverOrder(1, 1, "1234"));
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void shouldThrowInvalidOrderActionExceptionWhenStateIsNotDone() {
        order.setChefId(1);
        order.setState(OrderState.PENDING.label);
        order.setPin("1234");

        when(orderPersistencePort.getOrderById(1)).thenReturn(order);

        assertThrows(InvalidOrderActionException.class,
                () -> orderUseCase.deliverOrder(1, 1, "1234"));
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void shouldThrowInvalidOrderActionExceptionWhenStateIsDelivered() {
        order.setChefId(1);
        order.setState(OrderState.DELIVERED.label);
        order.setPin("1234");
        when(orderPersistencePort.getOrderById(1)).thenReturn(order);

        assertThrows(InvalidOrderActionException.class,
                () -> orderUseCase.deliverOrder(1, 1, "1234"));
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void shouldThrowWrongCredentialsExceptionWhenPinIsWrong() {
        order.setChefId(1);
        order.setState(OrderState.DONE.label);
        order.setPin("1234");
        when(orderPersistencePort.getOrderById(1)).thenReturn(order);

        assertThrows(WrongCredentialsException.class,
                () -> orderUseCase.deliverOrder(1, 1, "9999"));
        verify(orderLogsClientPort, never()).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void shouldDeliverOrderSuccessfully() {
        order.setChefId(1);
        order.setState(OrderState.DONE.label);
        order.setPin("1234");

        when(orderPersistencePort.getOrderById(1)).thenReturn(order);
        orderUseCase.deliverOrder(1, 1, "1234");

        assertEquals(OrderState.DELIVERED.label, order.getState());
        verify(orderPersistencePort).updateOrder(order);
        verify(orderLogsClientPort).logOrderStatusChange(any(OrderLogModel.class));
    }

    @Test
    void cancelOrder_shouldThrowOrderNotFound_whenNoOrderExists() {
        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(null);
        assertThrows(OrderNotFoundException.class, () -> orderUseCase.cancelOrder(1));
        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void cancelOrder_shouldThrowInvalidOrderAction_whenOrderIsNotPending() {
        OrderModel order = new OrderModel();
        order.setState(OrderState.DONE.label);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(order);

        assertThrows(InvalidOrderActionException.class, () -> orderUseCase.cancelOrder(1));
        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void cancelOrder_shouldUpdateOrderToCanceled_whenOrderIsPending() {
        OrderModel order = new OrderModel();
        order.setState(OrderState.PENDING.label);

        when(orderPersistencePort.getOrderByUserId(1)).thenReturn(order);

        orderUseCase.cancelOrder(1);

        ArgumentCaptor<OrderModel> captor = ArgumentCaptor.forClass(OrderModel.class);
        verify(orderPersistencePort).updateOrder(captor.capture());
        assertEquals(OrderState.CANCELED.label, captor.getValue().getState());
    }
}