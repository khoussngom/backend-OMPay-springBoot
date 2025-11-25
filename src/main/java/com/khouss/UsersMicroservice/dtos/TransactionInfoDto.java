package com.khouss.UsersMicroservice.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInfoDto {
    private String type;
    private String montant;
    private LocalDateTime date;
    private String codeMarchand;
    private String numero;
}