package com.pragma.powerup.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantModel {
    private int id;
    private String name;
    private String address;
    private int ownerId;
    private String phone;
    private String urlLogo;
    private String nit;
}
