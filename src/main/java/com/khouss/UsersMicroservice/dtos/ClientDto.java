package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(name = "ClientDto", description = "Détails du client liés à l'utilisateur")
@Data
public class ClientDto {
    private UUID id;
    private String prenom;
    private String nom;
    private String adresse;
    private String email;
    private String telephone;
}

