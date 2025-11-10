package com.khouss.UsersMicroservice.repo;

import com.khouss.UsersMicroservice.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompteRepository extends JpaRepository<Compte, UUID> {
    Optional<Compte> findByNumeroTelephone(String numeroTelephone);


    Optional<Compte> findByIdUser(UUID idUser);

    Optional<Compte> findByIdClient(UUID idClient);
}
