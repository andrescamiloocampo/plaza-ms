package com.pragma.powerup.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantPartialResponseDto {
    private int id;
    private String name;
    private String urlLogo;
    private String nit;
    private String address;
}
