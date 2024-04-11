package com.online.gallery.repository.media;


import com.online.gallery.model.media.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    List<Video> findAllByUserId(String userId);

    Optional<Video> findByNameAndUserId(String name, String userId);

    Optional<Video> findByIdAndUserId(String id, String userId);
}
