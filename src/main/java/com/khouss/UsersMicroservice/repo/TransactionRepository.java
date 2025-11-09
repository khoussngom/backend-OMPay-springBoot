package com.khouss.UsersMicroservice.repo;

import com.khouss.UsersMicroservice.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByCompteId(UUID compteId);
    List<Transaction> findByCompteDestId(UUID compteId);
}

