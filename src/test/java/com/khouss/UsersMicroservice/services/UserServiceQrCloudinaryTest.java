package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceQrCloudinaryTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private QrCodeService qrCodeService;

    @MockBean
    private CloudinaryService cloudinaryService;

    @MockBean
    private SmsService smsService;

    @MockBean
    private OtpService otpService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void saveUser_generates_qr_and_uploads_to_cloudinary() throws Exception {
        User toSave = new User();
        toSave.setUsername("testuser");
        toSave.setPassword("secret");
        toSave.setTelephone("774730039");

        User saved = new User();
        saved.setId(java.util.UUID.randomUUID());
        saved.setUsername("testuser");
        // same telephone as input to avoid confusion
        saved.setTelephone("774730039");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        byte[] qrBytes = new byte[]{1,2,3};
        // mock QR code generation and cloudinary upload
        when(qrCodeService.generateQrCodePng(anyString(), anyInt(), anyInt())).thenReturn(qrBytes);
        when(cloudinaryService.uploadImage(any(), anyString())).thenReturn("https://res.cloudinary.com/test/image/upload/qr_test.png");
        // éviter l'appel réel Twilio via OtpService : retourner un OTP fictif
        when(otpService.generateAndSendOtp(any())).thenReturn("123456");

        // captors to inspect what was passed to cloudinary
        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<String> publicIdCaptor = ArgumentCaptor.forClass(String.class);

        User result = userService.saveUser(toSave);

        assertThat(result).isNotNull();
        assertThat(result.getQrCodeUrl()).isEqualTo("https://res.cloudinary.com/test/image/upload/qr_test.png");

        verify(qrCodeService, times(1)).generateQrCodePng(eq(saved.getTelephone()), eq(250), eq(250));
        verify(cloudinaryService, times(1)).uploadImage(bytesCaptor.capture(), publicIdCaptor.capture());

        // vérifier que les octets passés à Cloudinary sont ceux générés par QrCodeService
        assertThat(bytesCaptor.getValue()).isEqualTo(qrBytes);
        // vérifier que le publicId respecte le format "qr_<id>"
        assertThat(publicIdCaptor.getValue()).isEqualTo("qr_" + saved.getId());
    }
}
