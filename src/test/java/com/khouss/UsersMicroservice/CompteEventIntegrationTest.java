package com.khouss.UsersMicroservice;

import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.repo.CompteRepository;
import com.khouss.UsersMicroservice.services.SmsService;
import com.khouss.UsersMicroservice.services.impl.CompteServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CompteEventIntegrationTest {

    @Autowired
    CompteServiceImpl compteService;

    @Autowired
    CompteRepository compteRepository;

    @MockBean
    SmsService smsService;

    @Test
    void quand_un_compte_est_créé_le_listener_envoie_un_sms() {
        Compte c = new Compte();
        c.setNumeroTelephone("+221774730039");
        c.setIdClient(UUID.randomUUID());
        c.setIdUser(UUID.randomUUID());

        Compte saved = compteService.creerCompte(c);

        verify(smsService, timeout(2000)).sendSMS(anyString(), anyString());
    }
}

