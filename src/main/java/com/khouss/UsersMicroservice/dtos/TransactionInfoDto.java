package com.khouss.UsersMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfoDto {
    private String type;
    private BigDecimal montant; // avec signe
    private LocalDateTime date;
    private String codeMarchand; // si applicable
}