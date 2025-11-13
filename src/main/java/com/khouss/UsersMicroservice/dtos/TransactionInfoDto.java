package com.khouss.UsersMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfoDto {
    private String type;
    private String montant; // avec signe comme string
    private LocalDateTime date;
    private String codeMarchand; // si applicable
}