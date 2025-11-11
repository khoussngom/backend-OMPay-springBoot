package com.khouss.UsersMicroservice.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.khouss.UsersMicroservice.repo.UserRepository;
import com.khouss.UsersMicroservice.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class OtpService {

    private final UserRepository userRepository;

    @Value("${TWILIO_ACCOUNT_SID:}")
    private String accountSid;
    @Value("${TWILIO_TOKEN:}")
    private String authToken;
    @Value("${TWILIO_FROM:}")
    private String fromNumber;

    public OtpService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateAndSendOtp(User user) throws Exception {
        int code = 100000 + new Random().nextInt(900000);
        String otp = String.valueOf(code);
        long expiry = Instant.now().plusSeconds(5 * 60).toEpochMilli();

        user.setOtp(otp);
        user.setOtpExpiry(expiry);
        userRepository.save(user);

        if (accountSid == null || accountSid.isEmpty() || authToken == null || authToken.isEmpty()) {
            // in test or missing credentials, do not attempt to send
            return otp;
        }

        Twilio.init(accountSid, authToken);
        String body = "Votre code OTP: " + otp + " (valide 5 minutes)";
        Message message = Message.creator(new PhoneNumber(user.getTelephone()), new PhoneNumber(fromNumber), body).create();
        return otp;
    }

    public boolean validateOtp(User user, String otp) {
        if (user.getOtp() == null || user.getOtpExpiry() == null) return false;
        if (!user.getOtp().equals(otp)) return false;
        if (Instant.now().toEpochMilli() > user.getOtpExpiry()) return false;
        // clear otp after use
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
        return true;
    }
}

