package com.khouss.UsersMicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

    public enum Type {
        DEPOT, TRANSFERT, PAIEMENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", length = 36, nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "compte_id", length = 36, columnDefinition = "char(36)")
    private UUID compteId; // référence simple pour flexibilité

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compte_fk")
    @JsonIgnore
    private Compte compte; // relation JPA pour navigation inverse

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private BigDecimal montant;

    private LocalDateTime date;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "compte_dest_id", length = 36, columnDefinition = "char(36)")
    private UUID compteDestId;

    private String codeMarchand;
}
