package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.infrastructure.out.feign.dto.response.UserResponseDto;
import com.pragma.powerup.domain.spi.IUserAuthClientPort;
import com.pragma.powerup.infrastructure.out.feign.UserAuthClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserAuthClientAdapter implements IUserAuthClientPort {

    private final UserAuthClient userAuthClient;

    @Override
    public UserResponseDto getUserById(String id) {
        return userAuthClient.getUserById(id);
    }
}
