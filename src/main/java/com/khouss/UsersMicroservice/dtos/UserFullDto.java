package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Schema(name = "UserFullDto", description = "Utilisateur complet avec client et admin si présents")
@Data
public class UserFullDto {
    private UUID id;
    private String username;
    private Boolean enabled;

    private ClientDto client;
    private AdminDto admin;
}

