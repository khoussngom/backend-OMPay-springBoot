package com.khouss.UsersMicroservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;



@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transferts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Entité représentant un transfert programmé")
public class Transfert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", length = 36, nullable = false, updatable = false, columnDefinition = "char(36)")
    @Schema(description = "ID unique du transfert", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "compte_id", length = 36, columnDefinition = "char(36)")
    @Schema(description = "ID du compte source", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID compteId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "compte_dest_id", length = 36, columnDefinition = "char(36)")
    @Schema(description = "ID du compte destinataire", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID compteDestId;

    @Column(nullable = false)
    @Schema(description = "Montant du transfert", example = "100.00")
    private BigDecimal montant;

    @Column(name = "date_programmee", nullable = false)
    @Schema(description = "Date et heure programmée pour l'exécution du transfert", example = "2025-11-24T12:00:00")
    private LocalDateTime dateProgrammee;

    @Column(nullable = false)
    @Schema(description = "Indique si le transfert a été exécuté", example = "false")
    private boolean executed = false;
}