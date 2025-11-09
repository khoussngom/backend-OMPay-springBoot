package com.khouss.UsersMicroservice.dtos;

import lombok.Data;

@Data
public class CompteCreationRequest {
    private String numeroTelephone;
    private String ancienNumeroTelephone;
    private String nouveauNumeroTelephone;
    private String username;
}
