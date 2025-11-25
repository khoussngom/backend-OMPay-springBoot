package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constant.OMPayMessages;
import com.khouss.UsersMicroservice.constant.OMPayResponse;
import com.khouss.UsersMicroservice.dtos.ScheduleTransferRequest;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.entities.Transfert;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.exception.CompteNotFoundException;
import com.khouss.UsersMicroservice.exception.DestinataireNotFoundException;
import com.khouss.UsersMicroservice.repo.CompteRepository;
import com.khouss.UsersMicroservice.repo.UserRepository;
import com.khouss.UsersMicroservice.services.TransfertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Transferts")
@RestController
@RequestMapping("/transferts")
@RequiredArgsConstructor
public class TransfertController {

    private final TransfertService transfertService;
    private final CompteRepository compteRepository;
    private final UserRepository userRepository;

    @PostMapping("/schedule")
    @Operation(summary = "Programmer un transfert", description = "Programme un transfert depuis le compte de l'utilisateur connecté vers un numéro de téléphone valide, à une date et heure précise (heure, minute, jour, mois, année uniquement)")
    public ResponseEntity<Map<String, Object>> scheduleTransfer(@RequestBody ScheduleTransferRequest request, Authentication authentication) {

        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Compte compteSource = compteRepository.findByIdUser(currentUser.getId())
                .orElseThrow(() -> new CompteNotFoundException("Compte source non trouvé pour l'utilisateur connecté"));

        Compte compteDest = compteRepository.findByNumeroTelephone(request.getNumeroTelephoneDest())
                .orElseThrow(() -> new DestinataireNotFoundException("Destinataire non trouvé avec ce numéro de téléphone"));


        if (request.getDateProgrammee().getSecond() != 0 || request.getDateProgrammee().getNano() != 0) {
            throw new IllegalArgumentException("La date programmée ne doit contenir que heure, minute, jour, mois et année");
        }

        Transfert transfert = transfertService.scheduleTransfer(
                compteSource.getId(),
                compteDest.getId(),
                request.getMontant(),
                request.getDateProgrammee()
        );
        return ResponseEntity.ok(OMPayResponse.success(transfert, OMPayMessages.TRANSFERT_SUCCES));
    }

    @GetMapping("/scheduled")
    @Operation(summary = "Lister les transferts programmés", description = "Retourne la liste des transferts programmés non encore exécutés")
    public ResponseEntity<Map<String, Object>> listScheduledTransfers() {
        List<Transfert> transferts = transfertService.listScheduledTransfers();
        return ResponseEntity.ok(OMPayResponse.success(transferts, OMPayMessages.LISTE_TRANSFERTS));
    }
}

