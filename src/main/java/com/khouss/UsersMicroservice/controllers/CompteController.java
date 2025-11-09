package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.constant.OMPayResponse;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.services.CompteService;
import com.khouss.UsersMicroservice.dtos.CompteCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comptes")
@RequiredArgsConstructor
public class CompteController implements CompteApi {

    private final CompteService compteService;

    @GetMapping
    @Override
    public ResponseEntity<Map<String, Object>> listerComptes() {
        List<Compte> comptes = compteService.listerComptes();
        return ResponseEntity.ok(OMPayResponse.success(comptes, OMPayMessages.LISTE_COMPTES));
    }

    @PostMapping
    @Override
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

    @PostMapping("/depot")
    @Override
    public ResponseEntity<Map<String, Object>> depotParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                              @RequestParam BigDecimal montant) {
        Compte compte = compteService.deposerParNumero(numeroTelephone, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.DEPOT_SUCCES));
    }

    @PostMapping("/transfert")
    @Override
    public ResponseEntity<Map<String, Object>> transfertParNumero(@RequestParam("source") String sourceNumero,
                                                                   @RequestParam("dest") String destNumero,
                                                                   @RequestParam BigDecimal montant) {
        Compte compte = compteService.transfertParNumero(sourceNumero, destNumero, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.TRANSFERT_SUCCES));
    }

    @PostMapping("/paiement")
    @Override
    public ResponseEntity<Map<String, Object>> paiementParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                                  @RequestParam("codeMarchand") String codeMarchand,
                                                                  @RequestParam BigDecimal montant) {
        Compte compte = compteService.paiementParNumero(numeroTelephone, codeMarchand, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.PAIEMENT_SUCCES));
    }
}
