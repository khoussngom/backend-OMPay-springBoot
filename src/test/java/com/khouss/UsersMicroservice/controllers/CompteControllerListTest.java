package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.khouss.UsersMicroservice.exception.GlobalExceptionHandler.class)
class CompteControllerListTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompteService compteService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtFilter jwtFilter;

    @Test
    void listerComptes_retourne200_avecEnvelope() throws Exception {
        when(compteService.listerComptes()).thenReturn(List.of());

        mockMvc.perform(get("/comptes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(OMPayMessages.LISTE_COMPTES.getMessage()))
                .andExpect(jsonPath("$.data").isArray());
    }
}
