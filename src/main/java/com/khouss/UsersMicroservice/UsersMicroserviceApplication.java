package com.khouss.UsersMicroservice;


import com.khouss.UsersMicroservice.services.SmsService;
import com.khouss.UsersMicroservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class UsersMicroserviceApplication {


    @Autowired
    UserService userService;
    public static void main(String[] args) {
        SpringApplication.run(UsersMicroserviceApplication.class, args);
    }

    @Autowired
    SmsService smsService;

    private final String numero = "+221774730039";
    private final String message = "Test Twilio depuis Spring Boot ";


}
