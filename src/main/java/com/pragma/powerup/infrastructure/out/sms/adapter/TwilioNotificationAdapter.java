package com.pragma.powerup.infrastructure.out.sms.adapter;

import com.pragma.powerup.domain.spi.INotificationPort;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwilioNotificationAdapter implements INotificationPort {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    @Value("${twilio.messaging-service-sid}")
    private String messagingServiceSid;

    private boolean initialized = false;

    private void init(){
        if (!initialized) {
            com.twilio.Twilio.init(accountSid, authToken);
            initialized = true;
        }
    }

    @Override
    public void sendNotification(String phoneNumber, String message) {
        init();
        Message.creator(
                new PhoneNumber(phoneNumber),
                messagingServiceSid,
                message
        ).create();
    }
}
