package com.khouss.UsersMicroservice.services;

public interface SmsService {
    void sendSMS(String to, String message) throws Exception;
}

