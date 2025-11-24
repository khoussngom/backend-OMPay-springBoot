package com.khouss.UsersMicroservice.repo;

import com.khouss.UsersMicroservice.entities.Transfert;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransfertRepository extends JpaRepository<Transfert, UUID> {
    List<Transfert> findByExecutedFalse();
    List<Transfert> findByExecutedFalseAndDateProgrammeeBefore(LocalDateTime date);
}