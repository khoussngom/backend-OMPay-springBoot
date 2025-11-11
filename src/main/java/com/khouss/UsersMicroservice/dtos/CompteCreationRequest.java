package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Requête pour ajouter un numéro à un client et créer un compte")
public class CompteCreationRequest {
    @Schema(description = "Ancien numéro de téléphone du client", example = "774730038")
    private String ancienNumeroTelephone;
    @Schema(description = "Nouveau numéro de téléphone du client", example = "774730039")
    private String nouveauNumeroTelephone;
    @Schema(description = "Nom d'utilisateur du client", example = "khoussngom")
    private String username;
}
