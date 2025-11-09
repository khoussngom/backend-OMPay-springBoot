package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(name = "UserResponse", description = "Représentation d'un utilisateur retournée par l'API")
@Data
public class UserResponse {
    @Schema(description = "Identifiant utilisateur (UUID)")
    private UUID id;

    @Schema(description = "Nom d'utilisateur")
    private String username;

    @Schema(description = "E-mail")
    private String email;

    @Schema(description = "Prénom")
    private String prenom;

    @Schema(description = "Nom")
    private String nom;

    @Schema(description = "Adresse")
    private String adresse;

    @Schema(description = "Téléphone")
    private String telephone;
}

