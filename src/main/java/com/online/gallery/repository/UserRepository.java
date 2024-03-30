package com.online.gallery.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.online.gallery.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(Object email);

    boolean existsByEmail(String email);
}
