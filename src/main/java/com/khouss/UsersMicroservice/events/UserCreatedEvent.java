package com.khouss.UsersMicroservice.events;


import com.khouss.UsersMicroservice.entities.User;
import org.springframework.context.ApplicationEvent;

public class UserCreatedEvent  extends ApplicationEvent{

    private final User user;
    public UserCreatedEvent (Object source,User user){
        super(source);
        this.user=user;
    }

    public User getUser() {
        return user;
    }
}