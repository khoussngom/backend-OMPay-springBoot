package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(name = "AdminDto", description = "Détails de l'admin liés à l'utilisateur")
@Data
public class AdminDto {
    private UUID id;
    private String prenom;
    private String nom;
}

