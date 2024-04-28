package com.online.gallery.repository.auth;

import com.online.gallery.entity.auth.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepo extends MongoRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByEmail(String email);
}
