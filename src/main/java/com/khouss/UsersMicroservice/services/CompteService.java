package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.Compte;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CompteService {
    Compte creerCompte(Compte compte);
    Compte deposer(UUID compteId, BigDecimal montant);
    Compte deposerParNumero(String numeroTelephone, BigDecimal montant);
    Compte transfertParNumero(String sourceNumero, String destNumero, BigDecimal montant);
    Compte paiementParNumero(String numeroTelephone, String codeMarchand, BigDecimal montant);
    Compte transfert(UUID compteSource, UUID compteDest, BigDecimal montant);
    Compte paiement(UUID compteId, String codeMarchand, BigDecimal montant);
    BigDecimal calculerSolde(UUID compteId);
    Compte creationAutomatiquePourUser(UUID userId, String numeroTelephone, UUID clientId);
    Compte creerComptePourClient(UUID clientId, String numeroTelephone);
    Compte creerComptePourUsername(String username, String numeroTelephone);
    List<Compte> listerComptes();
    Compte creerCompteMajNumeroPourUsername(String username, String ancienNumero, String nouveauNumero);
}
