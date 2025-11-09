package com.khouss.UsersMicroservice.events;

import com.khouss.UsersMicroservice.entities.Client;
import org.springframework.context.ApplicationEvent;

public class ClientCreatedEvent extends ApplicationEvent {
    private final Client client;
    public ClientCreatedEvent(Object source, Client client) {
        super(source);
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
