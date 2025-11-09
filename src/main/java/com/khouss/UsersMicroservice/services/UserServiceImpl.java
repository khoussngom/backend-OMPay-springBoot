package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.constants.Messages;
import com.khouss.UsersMicroservice.dtos.AdminDto;
import com.khouss.UsersMicroservice.dtos.ClientDto;
import com.khouss.UsersMicroservice.dtos.UserFullDto;
import com.khouss.UsersMicroservice.entities.Client;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.events.UserCreatedEvent;
import com.khouss.UsersMicroservice.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;


    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Override
    public List<User> findAllUser() {
        List<Object[]> rows = userRepository.findAllUsersWithClientAndAdminNative();
        List<User> users = new ArrayList<>();
        for (Object[] row : rows) {


            User u = new User();
            try {
                Object uId = row[0];
                if (uId != null) u.setId(UUID.fromString(uId.toString()));
            } catch (Exception e) {
                log.warn("Cannot parse user id from row: {}", row[0]);
            }
            u.setUsername(row[1] != null ? row[1].toString() : null);
            u.setPassword(row[2] != null ? row[2].toString() : null);
            u.setEnabled(row[3] != null ? Boolean.valueOf(row[3].toString()) : null);

            u.setEmail(row[5] != null ? row[5].toString() : null);
            u.setPrenom(row[6] != null ? row[6].toString() : null);
            u.setNom(row[7] != null ? row[7].toString() : null);
            u.setAdresse(row[8] != null ? row[8].toString() : null);
            u.setTelephone(row[9] != null ? row[9].toString() : null);

            users.add(u);
        }
        return users;
    }

    @Override
    public User findUserDetailsById(UUID id) {
        Optional<Object[]> opt = userRepository.findUserWithClientAndAdminById(id);
        if (opt.isEmpty()) return null;
        Object[] row = opt.get();
        User u = new User();
        try {
            Object uId = row[0];
            if (uId != null) u.setId(UUID.fromString(uId.toString()));
        } catch (Exception e) {
            log.warn("Cannot parse user id from row: {}", row[0]);
        }
        u.setUsername(row[1] != null ? row[1].toString() : null);
        u.setPassword(row[2] != null ? row[2].toString() : null);
        u.setEnabled(row[3] != null ? Boolean.valueOf(row[3].toString()) : null);

        u.setEmail(row[5] != null ? row[5].toString() : null);
        u.setPrenom(row[6] != null ? row[6].toString() : null);
        u.setNom(row[7] != null ? row[7].toString() : null);
        u.setAdresse(row[8] != null ? row[8].toString() : null);
        u.setTelephone(row[9] != null ? row[9].toString() : null);

        return u;
    }

    @Override
    public List<UserFullDto> findAllUsersFull() {
        List<Object[]> rows = userRepository.findAllUsersWithClientAndAdminNative();
        List<UserFullDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            UserFullDto dto = new UserFullDto();

            try { if (row[0] != null) dto.setId(UUID.fromString(row[0].toString())); } catch (Exception e) { }
            dto.setUsername(row[1] != null ? row[1].toString() : null);
            dto.setEnabled(row[3] != null ? Boolean.valueOf(row[3].toString()) : null);


            if (row[4] != null || row[5] != null) {
                ClientDto c = new ClientDto();
                try { if (row[4] != null) c.setId(UUID.fromString(row[4].toString())); } catch (Exception e) { }
                c.setEmail(row[5] != null ? row[5].toString() : null);
                c.setPrenom(row[6] != null ? row[6].toString() : null);
                c.setNom(row[7] != null ? row[7].toString() : null);
                c.setAdresse(row[8] != null ? row[8].toString() : null);
                c.setTelephone(row[9] != null ? row[9].toString() : null);
                dto.setClient(c);
            }


            if (row[10] != null) {
                AdminDto a = new AdminDto();
                try { if (row[10] != null) a.setId(UUID.fromString(row[10].toString())); } catch (Exception e) { }
                a.setPrenom(row[11] != null ? row[11].toString() : null);
                a.setNom(row[12] != null ? row[12].toString() : null);
                dto.setAdmin(a);
            }

            result.add(dto);
        }
        return result;
    }

    @Override
    public UserFullDto findUserFullById(UUID id) {
        Optional<Object[]> opt = userRepository.findUserWithClientAndAdminById(id);
        Object[] row = null;
        if (opt.isPresent()) {
            row = opt.get();
        } else {

            List<Object[]> rows = userRepository.findAllUsersWithClientAndAdminNative();
            for (Object[] r : rows) {
                if (r != null && r.length > 0 && r[0] != null && r[0].toString().equals(id.toString())) {
                    row = r;
                    break;
                }
            }
        }

        if (row == null || row.length == 0) return null;

        UserFullDto dto = new UserFullDto();
        try { if (row[0] != null) dto.setId(UUID.fromString(row[0].toString())); } catch (Exception e) { }
        dto.setUsername(row.length>1 && row[1] != null ? row[1].toString() : null);
        dto.setEnabled(row.length>3 && row[3] != null ? Boolean.valueOf(row[3].toString()) : null);

        if (row.length>4 && (row[4] != null || row[5] != null)) {
            com.khouss.UsersMicroservice.dtos.ClientDto c = new com.khouss.UsersMicroservice.dtos.ClientDto();
            try { if (row[4] != null) c.setId(UUID.fromString(row[4].toString())); } catch (Exception e) { }
            c.setEmail(row.length>5 && row[5] != null ? row[5].toString() : null);
            c.setPrenom(row.length>6 && row[6] != null ? row[6].toString() : null);
            c.setNom(row.length>7 && row[7] != null ? row[7].toString() : null);
            c.setAdresse(row.length>8 && row[8] != null ? row[8].toString() : null);
            c.setTelephone(row.length>9 && row[9] != null ? row[9].toString() : null);
            dto.setClient(c);
        }

        if (row.length>10 && row[10] != null) {
            com.khouss.UsersMicroservice.dtos.AdminDto a = new com.khouss.UsersMicroservice.dtos.AdminDto();
            try { if (row[10] != null) a.setId(UUID.fromString(row[10].toString())); } catch (Exception e) { }
            a.setPrenom(row.length>11 && row[11] != null ? row[11].toString() : null);
            a.setNom(row.length>12 && row[12] != null ? row[12].toString() : null);
            dto.setAdmin(a);
        }

        return dto;
    }

    public String genererToken(String username,String role) {
        OAuth2ResourceServerProperties.Jwt jwt = new OAuth2ResourceServerProperties.Jwt();
        return jwt.getIssuerUri() + "/" + username + "/" + role;
    }

    public User connexion(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    @Transactional
    public User saveUser(User user) {

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException(Messages.PASSWORD_EMPTY.getText());
        }
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException(Messages.USERNAME_EXISTS.getText());
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));


        User saved = userRepository.save(user);

        log.info("User saved with id={}", saved.getId());

        eventPublisher.publishEvent(new UserCreatedEvent(this, saved));

        return saved;
    }

    @Override
    public User FindByUsername(String username) {
        return userRepository.findByUsername(username);
    }



}
