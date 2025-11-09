package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.Client;
import com.khouss.UsersMicroservice.events.UserCreatedEvent;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountCreationListener {

    private static final Logger log = LoggerFactory.getLogger(AccountCreationListener.class);

    private final CompteService compteService;
    private final ClientRepository clientRepository;

    @EventListener
    public void onUserCreated(UserCreatedEvent event) {
        var user = event.getUser();
        if (user == null || user.getId() == null) {
            log.warn("UserCreatedEvent sans user valide");
            return;
        }
        // récupérer le client créé par ClientServiceImpl listener
        Client client = clientRepository.findByUserId(user.getId());
        UUID clientId = client != null ? client.getId() : null;
        String numero = user.getTelephone();
        try {
            var c = compteService.creationAutomatiquePourUser(user.getId(), numero, clientId);
            log.info("Compte auto créé pour user {} -> compte {}", user.getId(), c.getId());
        } catch (Exception e) {
            log.warn("Création automatique de compte ignorée: {}", e.getMessage());
        }
    }
}

