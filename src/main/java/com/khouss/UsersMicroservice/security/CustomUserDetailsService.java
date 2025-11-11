package com.khouss.UsersMicroservice.security;

import com.khouss.UsersMicroservice.entities.Admin;
import com.khouss.UsersMicroservice.entities.Client;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.repo.AdminRepository;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import com.khouss.UsersMicroservice.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.khouss.UsersMicroservice.constants.Messages.ROLE_USER;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(UserRepository userRepository, ClientRepository clientRepository, AdminRepository adminRepository) {
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Admin a = adminRepository.findByUserId(u.getId());
        Client c = clientRepository.findByUserId(u.getId());

        String role;
        if (a != null) {
            role = "ADMIN";
        } else if (c != null) {
            // if client has merchant flag or role stored in user, prioritize
            role = (u.getRole() != null) ? u.getRole() : "CLIENT";
        } else {
            role = (u.getRole() != null) ? u.getRole() : "CLIENT";
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(role)
                .disabled(!Boolean.TRUE.equals(u.getEnabled()))
                .build();
    }
}
