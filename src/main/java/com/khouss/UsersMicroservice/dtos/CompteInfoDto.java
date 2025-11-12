package com.khouss.UsersMicroservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompteInfoDto {
    private String numeroTelephone;
    private LocalDate dateOuverture;
    private List<TransactionInfoDto> transactions;
}