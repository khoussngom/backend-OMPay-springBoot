package com.khouss.UsersMicroservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", length = 36, nullable = false, updatable = false, columnDefinition = "char(36)")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private Boolean enabled;

    @Transient
    private String email;

    @Transient
    private String prenom;

    @Transient
    private String nom;

    @Transient
    private String adresse;

    @Transient
    private String telephone;
}