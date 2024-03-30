package com.online.gallery.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.online.gallery.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByEmail(String email);
}
