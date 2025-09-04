package com.pragma.powerup.infrastructure.out.feign.client;

import com.pragma.powerup.infrastructure.out.feign.dto.request.OrderLogRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-logs-service", url = "${order.logs.service.url}")
public interface IOrderLogsClient {
    @PostMapping("/logs/orders")
    void logOrderStatusChange(OrderLogRequestDto orderLogRequestDto);
}
