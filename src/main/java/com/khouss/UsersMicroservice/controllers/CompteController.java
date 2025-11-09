package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.constant.OMPayResponse;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.services.CompteService;
import com.khouss.UsersMicroservice.dtos.CompteCreationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Comptes")
@RestController
@RequestMapping("/comptes")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;

    @GetMapping
    @Operation(summary = "Lister les comptes", description = "Retourne la liste de tous les comptes avec leur solde courant")
    public ResponseEntity<Map<String, Object>> listerComptes() {
        List<Compte> comptes = compteService.listerComptes();
        return ResponseEntity.ok(OMPayResponse.success(comptes, OMPayMessages.LISTE_COMPTES));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> creerCompte(@RequestBody CompteCreationRequest request) {

        String ancien = request.getAncienNumeroTelephone();
        String nouveau = request.getNouveauNumeroTelephone();
        if ((ancien == null && nouveau == null) && request.getNumeroTelephone() != null) {
            nouveau = request.getNumeroTelephone();
        }
        Compte created;
        if (nouveau != null) {
            created = compteService.creerCompteMajNumeroPourUsername(request.getUsername(), ancien, nouveau);
        } else {

            created = compteService.creerComptePourUsername(request.getUsername(), request.getNumeroTelephone());
        }
        return ResponseEntity.ok(OMPayResponse.success(created, OMPayMessages.COMPTE_CREE_SUCCES));
    }

    @PostMapping("/{id}/depot")
    public ResponseEntity<Map<String, Object>> depot(@PathVariable("id") UUID id, @RequestParam BigDecimal montant) {
        Compte compte = compteService.deposer(id, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.DEPOT_SUCCES));
    }

    @PostMapping("/depot")
    @Operation(summary = "Dépôt par numéro", description = "Effectue un dépôt sur un compte via son numéro de téléphone")
    public ResponseEntity<Map<String, Object>> depotParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                              @RequestParam BigDecimal montant) {
        Compte compte = compteService.deposerParNumero(numeroTelephone, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.DEPOT_SUCCES));
    }

    @PostMapping("/transfert")
    @Operation(summary = "Transfert par numéro", description = "Effectue un transfert d'un numéro source vers un numéro destinataire")
    public ResponseEntity<Map<String, Object>> transfertParNumero(@RequestParam("source") String sourceNumero,
                                                                   @RequestParam("dest") String destNumero,
                                                                   @RequestParam BigDecimal montant) {
        Compte compte = compteService.transfertParNumero(sourceNumero, destNumero, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.TRANSFERT_SUCCES));
    }

    @PostMapping("/{id}/paiement")
    public ResponseEntity<Map<String, Object>> paiement(@PathVariable("id") UUID compteId,
                                                         @RequestParam("codeMarchand") String codeMarchand,
                                                         @RequestParam BigDecimal montant) {
        Compte compte = compteService.paiement(compteId, codeMarchand, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.PAIEMENT_SUCCES));
    }

    @PostMapping("/paiement")
    @Operation(summary = "Paiement par numéro", description = "Effectue un paiement marchand à partir d'un numéro de compte")
    public ResponseEntity<Map<String, Object>> paiementParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                                  @RequestParam("codeMarchand") String codeMarchand,
                                                                  @RequestParam BigDecimal montant) {
        Compte compte = compteService.paiementParNumero(numeroTelephone, codeMarchand, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.PAIEMENT_SUCCES));
    }

    @Deprecated
    @PostMapping("/{id}/transfert")
    public ResponseEntity<Map<String, Object>> transfert(@PathVariable("id") UUID sourceId,
                                                          @RequestParam("dest") UUID destId,
                                                          @RequestParam BigDecimal montant) {
        Compte compte = compteService.transfert(sourceId, destId, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.TRANSFERT_SUCCES));
    }
}
