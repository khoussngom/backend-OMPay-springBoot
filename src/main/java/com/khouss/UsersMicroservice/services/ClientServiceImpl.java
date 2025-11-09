package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.Client;
import com.khouss.UsersMicroservice.events.UserCreatedEvent;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);


    @Autowired
    ClientRepository clientRepository;



    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserCreated(UserCreatedEvent userCreatedEvent) {
        log.info("Received UserCreatedEvent: {}", userCreatedEvent == null ? "null" : userCreatedEvent.getUser());
        if (userCreatedEvent == null || userCreatedEvent.getUser() == null) {
            log.warn("UserCreatedEvent or user is null, skipping client creation");
            return;
        }

        Client client = new Client();

        Object idObj = userCreatedEvent.getUser().getId();
        if (idObj != null) {
            UUID userIdUuid = null;
            if (idObj instanceof UUID) {
                userIdUuid = (UUID) idObj;
            } else {
                try {
                    userIdUuid = UUID.fromString(idObj.toString());
                } catch (IllegalArgumentException e) {
                    log.warn("User id is not a valid UUID: {}", idObj);
                }
            }
            client.setUserId(userIdUuid);
        }

        // utiliser les champs transients fournis dans la requête
        client.setEmail(userCreatedEvent.getUser().getEmail());
        client.setPrenom(userCreatedEvent.getUser().getPrenom() != null ? userCreatedEvent.getUser().getPrenom() : "");
        client.setNom(userCreatedEvent.getUser().getNom() != null ? userCreatedEvent.getUser().getNom() : "");
        client.setAdresse(userCreatedEvent.getUser().getAdresse() != null ? userCreatedEvent.getUser().getAdresse() : "");
        client.setTelephone(userCreatedEvent.getUser().getTelephone() != null ? userCreatedEvent.getUser().getTelephone() : "");

        try {
            Client saved = clientRepository.save(client);
            log.info("Client created for userId={} with clientId={}", client.getUserId(), saved.getId());
        } catch (Exception e) {
            log.error("Failed to save Client for userId={}: {}", client.getUserId(), e.getMessage(), e);
        }
    }

    @Override
    public Client findByUserId(String userId) {
        try {
            return clientRepository.findByUserId(UUID.fromString(userId));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }
}
