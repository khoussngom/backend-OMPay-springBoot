package com.khouss.UsersMicroservice.controllers;

import com.khouss.UsersMicroservice.dtos.UserResponse;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.services.UserServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserServiceImpl userService;

    public UsersController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/solde")
    public ResponseEntity<?> getSolde(@RequestParam String telephone, @RequestParam String password) {
        // find compte by phone (Compte entity previously stores numeroTelephone). For simplicity, lookup user by username==telephone or telephone field
        User user = userService.FindByUsername(telephone);
        if (user == null) {
            // try find by telephone field
            var all = userService.findAllUser();
            user = all.stream().filter(u -> telephone.equals(u.getTelephone())).findFirst().orElse(null);
        }
        if (user == null) return ResponseEntity.status(404).body(Map.of("error","User not found"));
        User auth = userService.connexion(user.getUsername(), password);
        if (auth == null) return ResponseEntity.status(401).body(Map.of("error","Invalid credentials"));
        UserResponse resp = new UserResponse();
        BeanUtils.copyProperties(user, resp);
        return ResponseEntity.ok(Map.of("user", resp, "balance", user.getBalance()));
    }
}

