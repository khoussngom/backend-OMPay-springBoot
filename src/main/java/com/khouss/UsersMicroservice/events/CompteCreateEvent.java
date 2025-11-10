package com.khouss.UsersMicroservice.events;

import com.khouss.UsersMicroservice.entities.Compte;
import org.springframework.context.ApplicationEvent;

public class CompteCreateEvent extends ApplicationEvent {

    private final Compte compte;

    public CompteCreateEvent(Object source,Compte compte) {
        super(source);
        this.compte = compte;
    }

    public Compte getCompte() {
        return compte;
    }
}
