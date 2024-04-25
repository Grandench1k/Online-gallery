package com.online.gallery.repository.user;

import com.online.gallery.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByEmail(Object email);

    boolean existsByEmail(String email);
}
