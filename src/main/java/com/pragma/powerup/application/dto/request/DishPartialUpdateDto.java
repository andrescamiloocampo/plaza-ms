package com.pragma.powerup.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DishPartialUpdateDto {
    String description;
    BigDecimal price;
}
