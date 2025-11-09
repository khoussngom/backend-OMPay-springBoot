package com.khouss.UsersMicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comptes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", length = 36, nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String numeroTelephone;

    @OneToMany(mappedBy = "compte", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Transaction> transactions = new ArrayList<>();

    private LocalDate dateOuverture;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id_client", length = 36, columnDefinition = "char(36)")
    private UUID idClient;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id_user", length = 36, columnDefinition = "char(36)")
    private UUID idUser;

    @Transient
    private BigDecimal solde;
}
