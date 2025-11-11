package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.constants.Messages;
import com.khouss.UsersMicroservice.dtos.UserFullDto;
import com.khouss.UsersMicroservice.dtos.UserRequest;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class  UserController implements UserApi {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String test() {
        return Messages.RESPONSE_OK.getText();
    }

    @GetMapping
    public ResponseEntity<List<UserFullDto>> getAllUsers() {
        List<UserFullDto> users = userService.findAllUsersFull();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserFullDto> getUserById(@PathVariable("id") UUID id) {
        UserFullDto dto = userService.findUserFullById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        User u = new User();
        u.setUsername(userRequest.getUsername());
        u.setPassword(userRequest.getPassword());
        u.setEmail(userRequest.getEmail());
        u.setPrenom(userRequest.getPrenom());
        u.setNom(userRequest.getNom());
        u.setAdresse(userRequest.getAdresse());
        u.setTelephone(userRequest.getTelephone());

        try {
            User saved = userService.saveUser(u);
            log.info("Saved user: id={}, username={}", saved.getId(), saved.getUsername());
            UserFullDto dto = userService.findUserFullById(saved.getId());
            log.info("UserFullDto returned for id {} : {}", saved.getId(), dto);
            URI location = URI.create(String.format("/users/%s", saved.getId() == null ? "" : saved.getId().toString()));
            return ResponseEntity.created(location).body(dto);
        } catch (Exception e) {
            log.error("Failed to create user: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

}
