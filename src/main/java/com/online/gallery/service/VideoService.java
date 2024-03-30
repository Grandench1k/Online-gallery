package com.online.gallery.service;

import org.springframework.web.multipart.MultipartFile;
import com.online.gallery.model.Video;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    String generateLinkWithUserIdForS3Videos(String userId);

    List<Video> findAllVideos(String userId);

    byte[] findVideoById(String id, String userId);

    Video saveVideo(Video videoToSave, MultipartFile videoFile, String userId) throws IOException;

    Video updateVideoById(String id, Video video, String userId);

    Video deleteVideoById(String id, String userId);
}
