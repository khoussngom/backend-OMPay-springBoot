package com.khouss.UsersMicroservice.controllers;

// ...existing code imports...
import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.constant.OMPayResponse;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.services.CompteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comptes")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> creerCompte(@RequestBody Compte compte) {
        Compte created = compteService.creerCompte(compte);
        return ResponseEntity.ok(OMPayResponse.success(created, OMPayMessages.COMPTE_CREE_SUCCES));
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<Map<String, Object>> creerComptePourClient(@PathVariable UUID clientId, @RequestParam String numeroTelephone) {
        Compte created = compteService.creerComptePourClient(clientId, numeroTelephone);
        return ResponseEntity.ok(OMPayResponse.success(created, OMPayMessages.COMPTE_CREE_SUCCES));
    }

    // ...existing code endpoints depot/transfert/paiement...
}

