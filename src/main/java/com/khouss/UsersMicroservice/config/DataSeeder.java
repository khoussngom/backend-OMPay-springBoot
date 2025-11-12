package com.khouss.UsersMicroservice.config;

import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner init(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
          
            if (!userRepository.existsByUsername("marakhib")) {
                User admin = new User();
                admin.setUsername("marakhib");
                admin.setPassword(encoder.encode("marakhib"));
                admin.setEnabled(true);
                admin.setRole("ADMIN");
                admin.setBalance(BigDecimal.ZERO);
                userRepository.save(admin);
            }

            for (int i = 1; i <= 5; i++) {
                String code = String.format("MRC-%04d", i);
                if (!userRepository.existsByUsername(code)) {
                    User m = new User();
                    m.setUsername(code);
                    m.setPassword(encoder.encode("merchant" + i));
                    m.setEnabled(true);
                    m.setRole("MERCHANT");
                    m.setBalance(BigDecimal.ZERO);
                    userRepository.save(m);
                }
            }
        };
    }
}

