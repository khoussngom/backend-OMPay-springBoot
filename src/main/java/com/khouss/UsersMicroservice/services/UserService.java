package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.dtos.UserFullDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> findAllUser();
    User saveUser(User user) throws Exception;
    User FindByUsername(String username);

    User findUserDetailsById(UUID id);


    List<UserFullDto> findAllUsersFull();
    UserFullDto findUserFullById(UUID id);
}
