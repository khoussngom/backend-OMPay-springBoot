package com.khouss.UsersMicroservice.repo;

import com.khouss.UsersMicroservice.entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {
}

