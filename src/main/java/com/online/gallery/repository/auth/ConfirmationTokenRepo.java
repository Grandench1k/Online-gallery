package com.online.gallery.repository.auth;

import com.online.gallery.entity.auth.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepo extends MongoRepository<ConfirmationToken, String> {
    Optional<ConfirmationToken> findByUserId(String userId);
}
