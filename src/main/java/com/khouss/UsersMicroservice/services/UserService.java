package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.dtos.UserFullDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> findAllUser();
    User saveUser(User user);
    User FindByUsername(String username);

    User findUserDetailsById(UUID id);

    // new
    List<UserFullDto> findAllUsersFull();
    UserFullDto findUserFullById(UUID id);
}
