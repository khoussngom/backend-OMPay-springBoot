package com.khouss.UsersMicroservice.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TwilioSmsServiceTest {

    @Test
    void twilio_send_invokes_twilio_api() throws Exception {
        // instantiate service with test credentials
        SmsServiceImpl smsService = new SmsServiceImpl("+221774730039", "testSid", "testToken");

        try (MockedStatic<Twilio> twilioMock = Mockito.mockStatic(Twilio.class);
             MockedStatic<Message> messageMock = Mockito.mockStatic(Message.class)) {

            // mock Message.creator(...) to return a mock MessageCreator
            MessageCreator mc = mock(MessageCreator.class);
            when(mc.create()).thenReturn(mock(Message.class));

            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(mc);

            // call service with local number (without +221) to test normalization
            smsService.sendSMS("774730039", "Test message");

            // verify Twilio.init was called with the injected test credentials
            twilioMock.verify(() -> Twilio.init("testSid", "testToken"));

            // verify Message.creator was invoked with message content
            messageMock.verify(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), eq("Test message")));

            verify(mc, times(1)).create();
        }
    }
}
