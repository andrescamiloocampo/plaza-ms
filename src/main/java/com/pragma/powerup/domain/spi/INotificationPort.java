package com.pragma.powerup.domain.spi;

public interface INotificationPort {
    void sendNotification(String phoneNumber, String message);
}
