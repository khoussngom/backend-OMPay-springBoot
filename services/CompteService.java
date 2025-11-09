package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.Compte;
import java.math.BigDecimal;
import java.util.UUID;

public interface CompteService {
    Compte creerCompte(Compte compte);
    Compte creerComptePourClient(UUID clientId, String numeroTelephone);
    Compte creationAutomatiquePourUser(UUID userId, String numeroTelephone, UUID clientId);
    Compte deposer(UUID compteId, BigDecimal montant);
    Compte transfert(UUID compteSource, UUID compteDest, BigDecimal montant);
    Compte paiement(UUID compteId, String codeMarchand, BigDecimal montant);
    BigDecimal calculerSolde(UUID compteId);
}

