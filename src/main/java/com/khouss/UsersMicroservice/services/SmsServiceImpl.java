package com.khouss.UsersMicroservice.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final String fromPhoneNumber;
    private final String accountSid;
    private final String authToken;

    
    public SmsServiceImpl(@Value("${TWILIO_PHONE_NUMBER:${TWILIO_FROM:}}") String fromPhoneNumber,
                          @Value("${TWILIO_ACCOUNT_SID:}") String accountSid,
                          @Value("${TWILIO_AUTH_TOKEN:${TWILIO_TOKEN:}}") String authToken) {
        this.fromPhoneNumber = fromPhoneNumber;
        this.accountSid = accountSid;
        this.authToken = authToken;
    }

    @Override
    public void sendSMS(String destinataire, String text) {
        if (!StringUtils.hasText(destinataire) || !StringUtils.hasText(text)) {
            log.error("Numéro destinataire ou message vide");
            throw new IllegalArgumentException("Numéro destinataire ou message vide");
        }
        if (!StringUtils.hasText(accountSid) || !StringUtils.hasText(authToken) || !StringUtils.hasText(fromPhoneNumber)) {

            throw new IllegalArgumentException("Configuration Twilio incomplète. Veuillez définir TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN et TWILIO_FROM.");
        }

        String normalized = destinataire == null ? "" : destinataire.trim();
        if (normalized.startsWith("+")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.startsWith("221")) {
            normalized = "221" + normalized;
        }
        normalized = "+" + normalized;

        try {
            Twilio.init(accountSid, authToken);

            Message sent = Message.creator(
                    new PhoneNumber(normalized),
                    new PhoneNumber(fromPhoneNumber),
                    text
            ).create();

            log.info("SMS envoyé avec succès à {} | SID : {}", normalized, sent.getSid());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du SMS : {}", e.getMessage(), e);
            throw new RuntimeException("Impossible d'envoyer le SMS", e);
        }
    }
}