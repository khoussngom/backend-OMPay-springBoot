package com.khouss.UsersMicroservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Requête pour programmer un transfert")
public class ScheduleTransferRequest {
    @Schema(description = "Numéro de téléphone du destinataire", example = "+221771234567")
    private String numeroTelephoneDest;

    @Schema(description = "Montant du transfert", example = "100.00")
    private BigDecimal montant;

    @Schema(description = "Date et heure programmée pour le transfert (format ISO 8601, seulement heure, minute, jour, mois, année - secondes doivent être 00)", example = "2025-11-24T12:00:00")
    private LocalDateTime dateProgrammee;
}
