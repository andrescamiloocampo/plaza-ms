package com.pragma.powerup.domain.spi;

import com.pragma.powerup.infrastructure.out.feign.dto.response.UserResponseDto;

public interface IUserAuthClientPort {
    UserResponseDto getUserById(String id);
}
