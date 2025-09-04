package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderLogModel;

public interface IOrderLogsClientPort {
    void logOrderStatusChange(OrderLogModel orderLogModel);
}
