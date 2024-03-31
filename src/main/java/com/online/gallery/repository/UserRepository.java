package com.online.gallery.repository;

import com.online.gallery.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(Object email);

    boolean existsByEmail(String email);
}
