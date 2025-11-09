package com.khouss.UsersMicroservice.repo;

import com.khouss.UsersMicroservice.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Admin findByUserId(UUID userId);
}
