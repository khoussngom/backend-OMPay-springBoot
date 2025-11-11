package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.events.CompteCreateEvent;
import com.khouss.UsersMicroservice.repo.CompteRepository;
import com.khouss.UsersMicroservice.utils.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class EnvoieSmsListener {

    private final Logger logger = LoggerFactory.getLogger(EnvoieSmsListener.class);

    private final SmsService smsService;
    private final CompteRepository compteRepository;

    private String genererCodeOtp() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCompteCreated(CompteCreateEvent event) {
        processCompteEvent(event);
    }

    @org.springframework.context.event.EventListener
    public void onCompteCreatedImmediate(CompteCreateEvent event) {
        processCompteEvent(event);
    }

    private void processCompteEvent(CompteCreateEvent event) {
        logger.info("════════════════════════════════════════");
        logger.info("🔴 [EnvoieSmsListener.processCompteEvent] CALLED");
        logger.info("════════════════════════════════════════");

        logger.info("🔵 Received Compte Created: {}", event == null ? "null" : event.getCompte());
        if (event == null || event.getCompte() == null) {
            logger.warn("CompteEvent or compte is null, skipping compte creation");
            return;
        }
        var compte = event.getCompte();
        logger.info("🔵 Compte created: {}", compte);
        if (compte == null) {
            logger.warn("CompteCreateEvent sans compte valide");
            return;
        }

        logger.info("🔵 Numéro de téléphone du compte: {}", compte.getNumeroTelephone());

        var telephone = compte.getNumeroTelephone();
        if (!StringUtils.hasText(telephone)) {
            logger.warn("⚠️ Le compte {} n'a pas de numéro de téléphone valide", compte.getId());
            return;
        }

        // Normalise le numéro en +221...
        String normalized = PhoneNumberUtils.normalizeToSenegalFormat(telephone);
        if (normalized == null) {
            logger.warn("⚠️ Impossible de normaliser le numéro {} pour le compte {}", telephone, compte.getId());
            return;
        }
        logger.info("✅ Numéro normalisé: {} -> {}", telephone, normalized);

        // Mettre à jour et sauvegarder le compte avec le numéro normalisé si différent
        if (!normalized.equals(compte.getNumeroTelephone())) {
            compte.setNumeroTelephone(normalized);
            try {
                compteRepository.save(compte);
                logger.info("✅ Compte {} mis à jour avec le numéro normalisé {}", compte.getId(), normalized);
            } catch (Exception e) {
                logger.error("❌ Impossible de sauvegarder le compte {} après normalisation: {}", compte.getId(), e.getMessage(), e);
            }
        }

        String codeOtp = genererCodeOtp();
        String message = String.format("Bonjour, votre code OTP est %s. Votre compte a été créé avec succès.", codeOtp);

        try {
            logger.info("🔵 Envoi SMS à {} avec message: {}", normalized, message);
            smsService.sendSMS(normalized, message);
            logger.info("✅ SMS envoyé au {} pour le compte {}", normalized, compte.getId());
        } catch (Exception e) {
            logger.error("❌ Échec envoi SMS pour le compte {}: {}", compte.getId(), e.getMessage(), e);
        }
    }
}
