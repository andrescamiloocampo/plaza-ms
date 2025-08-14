package com.pragma.powerup.infrastructure.out.feign.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserResponseDto {
    private int id;
    private String name;
    private String lastname;
    private String phone;
    private LocalDate birthdate;
    private List<RoleResponseDto> roles;
}
