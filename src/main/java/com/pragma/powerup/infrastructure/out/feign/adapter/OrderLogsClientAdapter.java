package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.model.OrderLogModel;
import com.pragma.powerup.domain.spi.IOrderLogsClientPort;
import com.pragma.powerup.infrastructure.out.feign.client.IOrderLogsClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.IOrderLogRequestMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderLogsClientAdapter implements IOrderLogsClientPort {

    private final IOrderLogsClient orderLogsClient;
    private final IOrderLogRequestMapper orderLogRequestMapper;

    @Override
    public void logOrderStatusChange(OrderLogModel orderLogModel) {
        orderLogsClient.logOrderStatusChange(orderLogRequestMapper.toRequestDto(orderLogModel));
    }
}
