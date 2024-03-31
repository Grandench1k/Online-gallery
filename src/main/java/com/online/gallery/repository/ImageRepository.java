package com.online.gallery.repository;


import com.online.gallery.model.media.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    Optional<Image> findByNameAndUserId(String imageName, String userId);

    List<Image> findAllByUserId(String userId);

    Optional<Image> findByIdAndUserId(String id, String userId);
}
