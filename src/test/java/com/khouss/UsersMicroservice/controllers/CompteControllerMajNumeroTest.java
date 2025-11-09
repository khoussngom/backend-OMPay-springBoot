package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.dtos.CompteCreationRequest;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.services.CompteService;
import com.khouss.UsersMicroservice.services.UserService;
import com.khouss.UsersMicroservice.utils.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.khouss.UsersMicroservice.exception.GlobalExceptionHandler.class)
class CompteControllerMajNumeroTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompteService compteService;
    @MockBean
    private UserService userService;
    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void creerCompteMajNumero_retourneCompte() throws Exception {
        CompteCreationRequest req = new CompteCreationRequest();
        req.setUsername("testuser");
        req.setAncienNumeroTelephone("+770000000");
        req.setNouveauNumeroTelephone("+771111111");

        Compte compte = new Compte();
        compte.setId(UUID.randomUUID());
        compte.setNumeroTelephone("+771111111");

        when(compteService.creerCompteMajNumeroPourUsername(anyString(), anyString(), anyString())).thenReturn(compte);

        mockMvc.perform(post("/comptes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value(OMPayMessages.COMPTE_CREE_SUCCES.getMessage()))
            .andExpect(jsonPath("$.data.numeroTelephone").value("+771111111"));
    }
}

