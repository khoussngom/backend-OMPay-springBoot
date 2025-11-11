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

}

