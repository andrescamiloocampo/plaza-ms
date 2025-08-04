package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {
    private int id;
    private int customerId;
    private LocalDateTime date;
    private String state;
    private int chefId;
    private RestaurantModel restaurant;
}
