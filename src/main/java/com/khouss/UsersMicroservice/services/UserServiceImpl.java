package com.khouss.UsersMicroservice.services;

import com.khouss.UsersMicroservice.constants.Messages;
import com.khouss.UsersMicroservice.dtos.AdminDto;
import com.khouss.UsersMicroservice.dtos.ClientDto;
import com.khouss.UsersMicroservice.dtos.UserFullDto;
import com.khouss.UsersMicroservice.entities.Client;
import com.khouss.UsersMicroservice.entities.User;
import com.khouss.UsersMicroservice.entities.Compte;
import com.khouss.UsersMicroservice.events.UserCreatedEvent;
import com.khouss.UsersMicroservice.repo.UserRepository;
import com.khouss.UsersMicroservice.repo.ClientRepository;
import com.khouss.UsersMicroservice.repo.AdminRepository;
import com.khouss.UsersMicroservice.repo.CompteRepository;
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
import com.khouss.UsersMicroservice.utils.PhoneNumberUtils;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    CompteRepository compteRepository;


    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    com.khouss.UsersMicroservice.services.QrCodeService qrCodeService;

    @Autowired
    com.khouss.UsersMicroservice.services.CloudinaryService cloudinaryService;

    @Autowired
    com.khouss.UsersMicroservice.services.OtpService otpService;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User findUserDetailsById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<UserFullDto> findAllUsersFull() {
        List<com.khouss.UsersMicroservice.entities.User> users = userRepository.findAll();
        List<UserFullDto> result = new ArrayList<>();
        for (com.khouss.UsersMicroservice.entities.User u : users) {
            UserFullDto dto = new UserFullDto();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setEnabled(u.getEnabled());
            dto.setQrCodeUrl(u.getQrCodeUrl());

            Client client = clientRepository.findByUserId(u.getId());
            ClientDto c = null;
            if (client != null) {
                c = new ClientDto();
                c.setId(client.getId());
                c.setEmail(client.getEmail());
                c.setPrenom(client.getPrenom());
                c.setNom(client.getNom());
                c.setAdresse(client.getAdresse());

                c.setTelephone(client.getTelephone());
                c.setNumeroTelephone(null);
            }


            Optional<Compte> compteOpt = Optional.empty();
            try {
                compteOpt = compteRepository.findByIdUser(u.getId());
            } catch (Exception ignored) {}

            if (compteOpt.isEmpty() && c != null && c.getId() != null) {
                try {
                    compteOpt = compteRepository.findByIdClient(c.getId());
                } catch (Exception ignored) {}
            }

            if (compteOpt.isPresent()) {
                Compte compte = compteOpt.get();
                if (c == null) c = new ClientDto();
                c.setNumeroTelephone(compte.getNumeroTelephone());

                c.setTelephone(compte.getNumeroTelephone());
            } else {

                if (c == null) c = new ClientDto();
                if (c.getTelephone() == null && u.getTelephone() != null) {
                    c.setTelephone(u.getTelephone());
                }
            }

            if (c != null) dto.setClient(c);

            var a = adminRepository.findByUserId(u.getId());
            if (a != null) {
                AdminDto ad = new AdminDto();
                ad.setId(a.getId());
                ad.setPrenom(a.getPrenom());
                ad.setNom(a.getNom());
                dto.setAdmin(ad);
            }

            result.add(dto);
        }
        return result;
    }

    @Override
    public UserFullDto findUserFullById(UUID id) {
        Optional<com.khouss.UsersMicroservice.entities.User> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) return null;
        com.khouss.UsersMicroservice.entities.User u = optUser.get();
        UserFullDto dto = new UserFullDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEnabled(u.getEnabled());
        dto.setQrCodeUrl(u.getQrCodeUrl());

        Client client = clientRepository.findByUserId(u.getId());
        ClientDto c = null;
        if (client != null) {
            c = new ClientDto();
            c.setId(client.getId());
            c.setEmail(client.getEmail());
            c.setPrenom(client.getPrenom());
            c.setNom(client.getNom());
            c.setAdresse(client.getAdresse());
            c.setTelephone(client.getTelephone());
            c.setNumeroTelephone(null);
        }

        Optional<Compte> compteOpt = Optional.empty();
        try { compteOpt = compteRepository.findByIdUser(u.getId()); } catch (Exception ignored) {}
        if (compteOpt.isEmpty() && c != null && c.getId() != null) {
            try { compteOpt = compteRepository.findByIdClient(c.getId()); } catch (Exception ignored) {}
        }

        if (compteOpt.isPresent()) {
            Compte compte = compteOpt.get();
            if (c == null) c = new ClientDto();
            c.setNumeroTelephone(compte.getNumeroTelephone());
            c.setTelephone(compte.getNumeroTelephone());
        } else {
            if (c == null) c = new ClientDto();
            if (c.getTelephone() == null && u.getTelephone() != null) {
                c.setTelephone(u.getTelephone());
            }
        }

        if (c != null) dto.setClient(c);

        var a = adminRepository.findByUserId(u.getId());
        if (a != null) {
            AdminDto ad = new AdminDto();
            ad.setId(a.getId());
            ad.setPrenom(a.getPrenom());
            ad.setNom(a.getNom());
            dto.setAdmin(ad);
        }

        return dto;
    }

    public String genererToken(String username,String role) {
        OAuth2ResourceServerProperties.Jwt jwt = new OAuth2ResourceServerProperties.Jwt();
        return jwt.getIssuerUri() + "/" + username + "/" + role;
    }

    @Override
    public User connexion(String telephone, String password) {
        String normalizedTelephone = PhoneNumberUtils.normalizeToSenegalFormat(telephone);
        if (normalizedTelephone == null) return null;
        User user = userRepository.findByTelephone(normalizedTelephone);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    @Transactional
    public User saveUser(User user) throws Exception {

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException(Messages.PASSWORD_EMPTY.getText());
        }
        if (user.getId() == null && userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException(Messages.USERNAME_EXISTS.getText());
        }

        // keep raw password as encoded stored
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // default role
        if (user.getRole() == null) user.setRole("CLIENT");

        User saved = userRepository.save(user);

        log.info("User saved with id={}", saved.getId());

        // Normalize phone number (to +221...) and save normalized version if needed
        try {
            if (saved.getTelephone() != null) {
                String normalizedPhone = PhoneNumberUtils.normalizeToSenegalFormat(saved.getTelephone());
                if (normalizedPhone != null && !normalizedPhone.equals(saved.getTelephone())) {
                    saved.setTelephone(normalizedPhone);
                    saved = userRepository.save(saved);
                    log.info("Saved user with normalized telephone {}", normalizedPhone);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to normalize/save telephone for user {}: {}", saved.getId(), e.getMessage(), e);
        }

        // Generate QR code for the telephone if present
        String qrText = (saved.getTelephone() != null) ? saved.getTelephone() : saved.getUsername();
        log.info("🔵 Generating QR for text='{}' userId={}", qrText, saved.getId());
        byte[] png = qrCodeService.generateQrCodePng(qrText, 250, 250);
        log.info("✅ QR code PNG generated: {} bytes", png != null ? png.length : 0);

        String publicId = "qr_" + saved.getId();
        log.info("🔵 Uploading to Cloudinary with publicId={}", publicId);
        String url = cloudinaryService.uploadImage(png, publicId);
        log.info("✅ Cloudinary response: url={}", url);

        if (url != null && !url.isEmpty()) {
            saved.setQrCodeUrl(url);
            saved = userRepository.save(saved);
            log.info("✅ QR code URL saved to user: {}", url);
        } else {
            log.warn("⚠️ Cloudinary returned empty/null URL for user {} and publicId {}", saved.getId(), publicId);
        }

        // Generate OTP and send via Twilio
        try {
            otpService.generateAndSendOtp(saved);
        } catch (Exception e) {
            log.warn("Failed to generate/send OTP: {}", e.getMessage());
        }

        eventPublisher.publishEvent(new UserCreatedEvent(this, saved));

        return saved;
    }

    @Override
    public User FindByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User FindByTelephone(String telephone) {
        String normalizedTelephone = PhoneNumberUtils.normalizeToSenegalFormat(telephone);
        if (normalizedTelephone == null) return null;
        return userRepository.findByTelephone(normalizedTelephone);
    }



}
