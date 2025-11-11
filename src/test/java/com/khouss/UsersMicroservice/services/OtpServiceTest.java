package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OtpServiceTest {

    @Test
    public void testGenerateAndValidateOtp() throws Exception {
        UserRepository repo = Mockito.mock(UserRepository.class);
        OtpService service = new OtpService(repo);

        User u = new User();
        u.setUsername("testu");
        u.setTelephone("+221700000000");

        Mockito.when(repo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        String otp = service.generateAndSendOtp(u);
        assertNotNull(otp);
        assertEquals(6, otp.length());
        boolean valid = service.validateOtp(u, otp);
        assertTrue(valid);
    }
}

