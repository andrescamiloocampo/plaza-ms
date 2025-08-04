package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.infrastructure.out.feign.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-auth-service", url = "${user.auth.service.url}")
public interface UserAuthClient {
    @GetMapping("/user/{id}")
    UserResponseDto getUserById(@PathVariable("id") String id);
}
