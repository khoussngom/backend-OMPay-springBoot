package com.khouss.UsersMicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.khouss.UsersMicroservice.validators.ValidCni;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private Boolean enabled = false;

    // persisted contact fields
    private String email;
    private String prenom;
    private String nom;
    private String adresse;
    private String telephone;

    // new fields
    @Column(unique = true)
    @ValidCni
    private String cni;

    private String role; // e.g., ADMIN, CLIENT, MERCHANT

    private String qrCodeUrl;

    private String otp;

    private Long otpExpiry; // epoch millis

    private BigDecimal balance = BigDecimal.ZERO;
}