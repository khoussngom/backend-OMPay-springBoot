package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.annotations.ApiResponseCreated;
import com.khouss.UsersMicroservice.dtos.CompteCreationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "Comptes", description = "API pour la gestion des comptes et transactions par numéro")
public interface CompteApi {

    @Operation(summary = "Lister les comptes", description = "Retourne la liste de tous les comptes avec leur solde courant")
    ResponseEntity<Map<String, Object>> listerComptes();

    @ApiResponseCreated
    @Operation(summary = "Créer un compte", description = "Création d'un compte à partir du username et d'un numéro (ancien/nouveau gérés)")
    ResponseEntity<Map<String, Object>> creerCompte(@RequestBody CompteCreationRequest request);

    @Operation(summary = "Dépôt par numéro", description = "Effectue un dépôt via un numéro de téléphone")
    ResponseEntity<Map<String, Object>> depotParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                       @RequestParam BigDecimal montant);

    @Operation(summary = "Transfert par numéro", description = "Effectue un transfert d'un numéro source vers un numéro destinataire")
    ResponseEntity<Map<String, Object>> transfertParNumero(@RequestParam("source") String sourceNumero,
                                                           @RequestParam("dest") String destNumero,
                                                           @RequestParam BigDecimal montant);

    @Operation(summary = "Paiement par numéro", description = "Effectue un paiement marchand via un numéro de téléphone")
    ResponseEntity<Map<String, Object>> paiementParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                          @RequestParam("codeMarchand") String codeMarchand,
                                                          @RequestParam BigDecimal montant);
}

