package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.events.CompteCreateEvent;
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
        // Listener de secours si l'événement est publié hors transaction ou si le listener transactionnel ne fonctionne pas.
        processCompteEvent(event);
    }

    private void processCompteEvent(CompteCreateEvent event) {
        logger.info("Received Compte Created: {}", event == null ? "null" : event.getCompte());
        if (event == null || event.getCompte() == null) {
            logger.warn("CompteEvent or compte is null, skipping compte creation");
            return;
        }
        var compte = event.getCompte();
        logger.info("Compte created: {}", compte);
        if (compte == null) {
            logger.warn("CompteCreateEvent sans compte valide");
            return;
        }


        logger.info("Numéro de téléphone du compte: {}", compte.getNumeroTelephone());

        var telephone = compte.getNumeroTelephone();
        if (!StringUtils.hasText(telephone)) {
            logger.warn("Le compte {} n'a pas de numéro de téléphone valide", compte.getId());
            return;
        }

        String codeOtp = genererCodeOtp();
        String message = String.format("Bonjour, votre code OTP est %s. Votre compte a été créé avec succès.", codeOtp);

        try {
            smsService.sendSMS(telephone, message);
            logger.info("SMS envoyé au {} pour le compte {}", telephone, compte.getId());
        } catch (Exception e) {
            logger.error("Échec envoi SMS pour le compte {}: {}", compte.getId(), e.getMessage(), e);
        }
    }
}
