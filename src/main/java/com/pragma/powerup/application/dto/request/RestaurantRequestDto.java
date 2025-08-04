package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantRequestDto {
    private String name;
    private String address;
    private int ownerId;
    private String phone;
    private String urlLogo;
    private String nit;
}
