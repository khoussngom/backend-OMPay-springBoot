package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "UserRequest", description = "Payload pour créer un utilisateur")
@Data
public class UserRequest {
    @Schema(description = "Nom d'utilisateur", example = "Khoussngom", required = true)
    private String username;

    @Schema(description = "Mot de passe", example = "marakhib", required = true)
    private String password;

    @Schema(description = "Adresse e-mail", example = "khoussn@gmail.com")
    private String email;

    @Schema(description = "Prénom", example = "khouss")
    private String prenom;

    @Schema(description = "Nom", example = "ngom")
    private String nom;

    @Schema(description = "Adresse postale", example = "malibu,golf nord")
    private String adresse;

    @Schema(description = "Numéro de téléphone", example = "+774730039")
    private String telephone;

    @Schema(description = "CNI (Senegal)", example = "19876543210987")
    private String cni;

    @Schema(description = "Role (ADMIN, CLIENT, MERCHANT)")
    private String role;
}
