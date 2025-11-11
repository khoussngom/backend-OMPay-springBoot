package com.khouss.UsersMicroservice.repo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.khouss.UsersMicroservice.entities.Client;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
          Client findByUserId(UUID userId);
          List<Client> findAllByUserId(UUID userId);
          Client findByEmail(String email);
}
