package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.events.CompteCreateEvent;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

public class EnvoieSmsListener {

    private final Logger logger = LoggerFactory.getLogger(EnvoieSmsListener.class);

    @Autowired
     CompteService compteService;

    @Autowired
     ClientRepository clientRepository;

    @Autowired
    SmsService smsService;

    private String genererCodeOtp(){
        int code = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    @EventListener
    public void onCompteCreated(CompteCreateEvent event) {
        var compte = event.getCompte();
        if (compte == null) {
            logger.warn("CompteCreateEvent sans compte valide");
            return;
        }


        var telephone = compte.getNumeroTelephone();
        if (telephone == null || telephone.isEmpty()) {
            logger.warn("Le client {} n'a pas de numéro de téléphone valide");
            return;
        }

        String codeOtp = genererCodeOtp();
        String message = String.format("Bonjour %s, votre compte %s a été créé avec succès.", codeOtp);
        smsService.sendSMS(telephone, message);
    }
}
