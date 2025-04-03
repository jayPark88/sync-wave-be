package com.parker.common.jpa.repository;

import com.parker.common.jpa.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
    List<PasswordResetTokenEntity> findByEmail(String email);
}
