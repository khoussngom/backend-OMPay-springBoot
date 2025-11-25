package com.khouss.UsersMicroservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.dtos.CompteCreationRequest;
import com.khouss.UsersMicroservice.exception.ClientNotFoundException;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.khouss.UsersMicroservice.exception.GlobalExceptionHandler.class)
class CompteControllerTest {

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
    void creerCompte_retourne404_siClientInexistant() throws Exception {

        CompteCreationRequest req = new CompteCreationRequest();
        req.setUsername("khoussngom");
        req.setAncienNumeroTelephone("774730038");
        req.setNouveauNumeroTelephone("774730039");

        when(compteService.creerCompteMajNumeroPourUsername(anyString(), anyString(), anyString()))
                .thenThrow(new ClientNotFoundException(OMPayMessages.CLIENT_INEXISTANT.getMessage()));


        mockMvc.perform(post("/comptes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(OMPayMessages.CLIENT_INEXISTANT.getMessage()));
    }
}
