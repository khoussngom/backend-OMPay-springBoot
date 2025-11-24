package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.Transfert;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface TransfertService {
    Transfert scheduleTransfer(UUID compteId, UUID compteDestId, BigDecimal montant, LocalDateTime dateProgrammee);
    List<Transfert> listScheduledTransfers();
    void executer(Transfert transfert);
}