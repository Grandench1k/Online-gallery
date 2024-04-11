package com.online.gallery.service.media.video;

import com.online.gallery.model.media.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    String generateLinkWithUserIdForS3Videos(String userId);

    List<Video> findAllVideos(String userId);

    byte[] findVideoById(String videoId, String userId);

    String getFileFormat(MultipartFile imageFile);

    Video saveVideo(Video videoToSave, MultipartFile videoFile, String userId) throws IOException;

    Video updateVideoById(String videoId, Video video, String userId);

    Video deleteVideoById(String videoId, String userId);
}
