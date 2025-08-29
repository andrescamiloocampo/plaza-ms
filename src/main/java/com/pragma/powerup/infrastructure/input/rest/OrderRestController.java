package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER','EMPLOYEE','CUSTOMER')")
    @Operation(summary = "Make order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created", content = @Content),
            @ApiResponse(responseCode = "409", description = "The user has orders in process", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden there is no authenticated user", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> makeOrder(@RequestBody OrderRequestDto orderRequestDto, Authentication authentication) {
        int userId = Integer.parseInt(authentication.getPrincipal().toString());
        orderHandler.makeOrder(orderRequestDto, userId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(summary = "Change order status and assign employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status changed", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden authentication required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Illegal request params provided", content = @Content)
    })
    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> changeOrderStatus(@PathVariable int orderId,
                                                  Authentication authentication) {
        int employeeId = Integer.parseInt(authentication.getPrincipal().toString());
        orderHandler.updateOrder(orderId,employeeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Notify order ready")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order ready notified", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden authentication required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Illegal request params provided", content = @Content)
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<Void> notifyOrderReady(@PathVariable int orderId,
                                                  Authentication authentication) {
        int employeeId = Integer.parseInt(authentication.getPrincipal().toString());
        orderHandler.notifyOrderReady(orderId,employeeId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','OWNER','EMPLOYEE')")
    @Operation(summary = "Get orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden authentication required", content = @Content),
            @ApiResponse(responseCode = "400", description = "Illegal request params provided", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam(name = "page", defaultValue = "0") int page,
                                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                                            @RequestParam(name = "state", defaultValue = "", required = false) String state,
                                                            Authentication authentication) {
        int userId = Integer.parseInt(authentication.getPrincipal().toString());
        return ResponseEntity.ok(orderHandler.getOrders(page, size, state, userId));
    }
}
