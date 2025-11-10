package com.khouss.UsersMicroservice.services;

public interface SmsService {

    public void sendSMS(String destinataire, String message);
}
