package com.online.gallery.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.online.gallery.model.ConfirmationToken;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, String> {
    Optional<ConfirmationToken> findByUserId(String userId);
}
