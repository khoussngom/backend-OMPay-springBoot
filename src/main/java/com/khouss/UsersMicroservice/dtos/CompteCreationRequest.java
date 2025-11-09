package com.khouss.UsersMicroservice.dtos;

import lombok.Data;

@Data
public class CompteCreationRequest {
    private String numeroTelephone; // legacy (peut servir de nouveau numero si les autres champs sont absents)
    private String ancienNumeroTelephone; // numéro actuel enregistré côté client pour vérification
    private String nouveauNumeroTelephone; // numéro à affecter avant création du compte
    private String username; // identité pour retrouver user/client
}
