package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.constant.OMPayResponse;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.entities.Transaction;
import com.khouss.UsersMicroservice.repo.TransactionRepository;
import com.khouss.UsersMicroservice.services.CompteService;
import com.khouss.UsersMicroservice.services.UserService;
import com.khouss.UsersMicroservice.dtos.CompteCreationRequest;
import com.khouss.UsersMicroservice.dtos.CompteInfoDto;
import com.khouss.UsersMicroservice.dtos.TransactionInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Comptes")
@RestController
@RequestMapping("/comptes")
@RequiredArgsConstructor
public class CompteController {

    private final CompteService compteService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    @GetMapping
    @Operation(summary = "Lister les comptes", description = "Retourne la liste de tous les comptes avec leur solde courant")
    public ResponseEntity<Map<String, Object>> listerComptes() {
        List<Compte> comptes = compteService.listerComptes();
        return ResponseEntity.ok(OMPayResponse.success(comptes, OMPayMessages.LISTE_COMPTES));
    }

    @PostMapping
    @Operation(summary = "Ajouter un numéro à un client", description = "Change le numéro de téléphone d'un client et crée un compte avec le nouveau numéro")
    public ResponseEntity<Map<String, Object>> creerCompte(@RequestBody CompteCreationRequest request) {

        String ancien = request.getAncienNumeroTelephone();
        String nouveau = request.getNouveauNumeroTelephone();
        String username = request.getUsername();

        if (ancien == null || nouveau == null || username == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "ancienNumeroTelephone, nouveauNumeroTelephone, and username are required"));
        }

        Compte created = compteService.creerCompteMajNumeroPourUsername(username, ancien, nouveau);
        return ResponseEntity.ok(OMPayResponse.success(created, OMPayMessages.COMPTE_CREE_SUCCES));
    }

    // @PostMapping("/{id}/depot")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<Map<String, Object>> depot(@PathVariable("id") UUID id, @RequestParam BigDecimal montant) {
    //     Compte compte = compteService.deposer(id, montant);
    //     return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.DEPOT_SUCCES));
    // }

    @PostMapping("/depot")
    @Operation(summary = "Dépôt par numéro", description = "Effectue un dépôt sur un compte via son numéro de téléphone")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> depotParNumero(@RequestParam("numeroTelephone") String numeroTelephone,
                                                               @RequestParam BigDecimal montant) {
        Compte compte = compteService.deposerParNumero(numeroTelephone, montant);
        return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.DEPOT_SUCCES));
    }

    @PostMapping("/transfert")
    @Operation(summary = "Transfert par numéro", description = "Effectue un transfert du compte connecté vers un numéro destinataire")
    public ResponseEntity<Map<String, Object>> transfertParNumero(@RequestParam("dest") String destNumero,
                                                                   @RequestParam BigDecimal montant) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = userService.FindByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        // Find the compte for the user
        var comptes = compteService.listerComptes().stream()
                .filter(c -> c.getIdUser() != null && c.getIdUser().equals(user.getId()))
                .toList();
        if (comptes.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Compte not found"));
        }
        Compte compte = comptes.get(0);
        String sourceNumero = compte.getNumeroTelephone();
        if (sourceNumero == null || sourceNumero.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Numéro de téléphone du compte invalide"));
        }
        Compte updated = compteService.transfertParNumero(sourceNumero, destNumero, montant);
        return ResponseEntity.ok(OMPayResponse.success(updated, OMPayMessages.TRANSFERT_SUCCES));
    }

    // @PostMapping("/{id}/paiement")
    // public ResponseEntity<Map<String, Object>> paiement(@PathVariable("id") UUID compteId,
    //                                                      @RequestParam("codeMarchand") String codeMarchand,
    //                                                      @RequestParam BigDecimal montant) {
    //     Compte compte = compteService.paiement(compteId, codeMarchand, montant);
    //     return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.PAIEMENT_SUCCES));
    // }

    @PostMapping("/paiement")
    @Operation(summary = "Paiement marchand", description = "Effectue un paiement marchand avec le numéro du compte connecté. Le paramètre 'marchand' peut être un code marchand (ex: MRC001) ou un numéro de téléphone marchand.")
    public ResponseEntity<Map<String, Object>> paiement(@RequestParam("marchand") String marchand,
                                                         @RequestParam BigDecimal montant) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = userService.FindByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        // Find the compte for the user
        var comptes = compteService.listerComptes().stream()
                .filter(c -> c.getIdUser() != null && c.getIdUser().equals(user.getId()))
                .toList();
        if (comptes.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Compte not found"));
        }
        Compte compte = comptes.get(0);
        String numeroTelephone = compte.getNumeroTelephone();
        if (numeroTelephone == null || numeroTelephone.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Numéro de téléphone du compte invalide"));
        }
        Compte updated = compteService.paiementParNumero(numeroTelephone, marchand, montant);
        return ResponseEntity.ok(OMPayResponse.success(updated, OMPayMessages.PAIEMENT_SUCCES));
    }

    // @Deprecated
    // @PostMapping("/{id}/transfert")
    // public ResponseEntity<Map<String, Object>> transfert(@PathVariable("id") UUID sourceId,
    //                                                        @RequestParam("dest") UUID destId,
    //                                                        @RequestParam BigDecimal montant) {
    //     Compte compte = compteService.transfert(sourceId, destId, montant);
    //     return ResponseEntity.ok(OMPayResponse.success(compte, OMPayMessages.TRANSFERT_SUCCES));
    // }

    @GetMapping("/solde")
    @Operation(summary = "Consulter le solde du compte", description = "Retourne le solde du compte de l'utilisateur connecté")
    public ResponseEntity<Map<String, Object>> getSolde() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = userService.FindByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        // Assuming one compte per user
        var comptes = compteService.listerComptes().stream()
                .filter(c -> c.getIdUser() != null && c.getIdUser().equals(user.getId()))
                .toList();
        if (comptes.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Compte not found"));
        }
        Compte compte = comptes.get(0);
        BigDecimal solde = compte.getSolde() != null ? compte.getSolde() : BigDecimal.ZERO;
        return ResponseEntity.ok(OMPayResponse.success(Map.of("solde", solde), OMPayMessages.LISTE_COMPTES));
    }

    @GetMapping("/me")
    @Operation(summary = "Afficher les informations du compte connecté", description = "Retourne les informations du compte de l'utilisateur connecté avec les transactions signées")
    public ResponseEntity<Map<String, Object>> getMonCompte() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        var user = userService.FindByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        // Assuming one compte per user
        var comptes = compteService.listerComptes().stream()
                .filter(c -> c.getIdUser() != null && c.getIdUser().equals(user.getId()))
                .toList();
        if (comptes.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Compte not found"));
        }
        Compte compte = comptes.get(0);

        // Récupérer les transactions sortantes et entrantes
        List<Transaction> transactionsSortantes = transactionRepository.findByCompteId(compte.getId());
        List<Transaction> transactionsEntrantes = transactionRepository.findByCompteDestId(compte.getId());

        List<TransactionInfoDto> transactionInfos = new ArrayList<>();

        // Traiter les transactions sortantes
        for (Transaction t : transactionsSortantes) {
            BigDecimal montant = t.getMontant();
            String montantStr;
            if (t.getType() == Transaction.Type.DEPOT) {
                montantStr = "+" + montant.toString();
            } else {
                montantStr = "-" + montant.toString();
            }
            transactionInfos.add(new TransactionInfoDto(t.getType().toString(), montantStr, t.getDate(), t.getCodeMarchand()));
        }

        // Traiter les transactions entrantes (transferts entrants)
        for (Transaction t : transactionsEntrantes) {
            if (t.getType() == Transaction.Type.TRANSFERT) {
                String montantStr = "+" + t.getMontant().toString();
                transactionInfos.add(new TransactionInfoDto("TRANSFERT_ENTRANT", montantStr, t.getDate(), null));
            }
        }

        // Trier par date décroissante
        transactionInfos.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        // Calculer le solde
        BigDecimal solde = compteService.calculerSolde(compte.getId());

        CompteInfoDto compteInfo = new CompteInfoDto(compte.getNumeroTelephone(), compte.getDateOuverture(), solde, transactionInfos);

        return ResponseEntity.ok(OMPayResponse.success(compteInfo, OMPayMessages.LISTE_COMPTES));
    }
}
