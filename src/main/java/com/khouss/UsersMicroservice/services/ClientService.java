package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.Client;

import java.util.List;

public interface ClientService {
    List<Client> findAll();
    Client createClient(Client client);
    Client findByUserId(String userId);
    Client findByEmail(String email);
}
