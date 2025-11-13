package com.khouss.UsersMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompteInfoDto {
    private String numeroTelephone;
    private LocalDate dateOuverture;
    private BigDecimal solde;
    private List<TransactionInfoDto> transactions;
}