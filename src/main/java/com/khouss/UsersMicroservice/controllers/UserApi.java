package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.annotations.ApiResponseCreated;
import com.khouss.UsersMicroservice.dtos.UserRequest;
import com.khouss.UsersMicroservice.dtos.UserFullDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "API pour la gestion des utilisateurs")
public interface UserApi {

    @Operation(
            summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste de tous les utilisateurs dans la base de données."
    )
    ResponseEntity<List<UserFullDto>> getAllUsers();

    @Operation(
            summary = "Récupérer un utilisateur par id",
            description = "Retourne l'utilisateur complet (user + client + admin)"
    )
    ResponseEntity<UserFullDto> getUserById(@PathVariable("id") UUID id);

    @ApiResponseCreated
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = "Ajoute un utilisateur dans la base de données {username, password}."
    )
    ResponseEntity<UserFullDto> createUser(@RequestBody UserRequest userRequest);
}
