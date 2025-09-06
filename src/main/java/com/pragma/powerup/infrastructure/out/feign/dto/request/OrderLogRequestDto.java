package com.pragma.powerup.infrastructure.out.feign.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderLogRequestDto {
    private Long orderId;
    private Long chefId;
    private Long customerId;
    private Long restaurantId;
    private List<OrderStatusRequestDto> statusChanges;
}
