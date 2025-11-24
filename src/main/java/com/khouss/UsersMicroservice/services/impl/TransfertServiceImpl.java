package com.khouss.UsersMicroservice.services.impl;

import com.khouss.UsersMicroservice.entities.Transfert;
import com.khouss.UsersMicroservice.repo.TransfertRepository;
import com.khouss.UsersMicroservice.services.CompteService;
import com.khouss.UsersMicroservice.services.TransfertService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TransfertServiceImpl implements TransfertService {

    private final TransfertRepository transfertRepository;
    private final CompteService compteService;

    @Override
    public Transfert scheduleTransfer(UUID compteId, UUID compteDestId, BigDecimal montant, LocalDateTime dateProgrammee) {
        Transfert transfert = new Transfert();
        transfert.setCompteId(compteId);
        transfert.setCompteDestId(compteDestId);
        transfert.setMontant(montant);
        transfert.setDateProgrammee(dateProgrammee);
        transfert.setExecuted(false);
        return transfertRepository.save(transfert);
    }

    @Override
    public List<Transfert> listScheduledTransfers() {
        return transfertRepository.findByExecutedFalse();
    }

    @Override
    public void executer(Transfert transfert) {
        compteService.transfert(transfert.getCompteId(), transfert.getCompteDestId(), transfert.getMontant());
        transfert.setExecuted(true);
        transfertRepository.save(transfert);
    }
}