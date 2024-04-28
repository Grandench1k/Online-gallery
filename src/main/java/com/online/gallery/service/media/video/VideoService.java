package com.online.gallery.service.media.video;

import com.online.gallery.dto.request.VideoDetailsRequest;
import com.online.gallery.dto.request.VideoFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Video;

import java.util.List;

public interface VideoService {
    String generateLinkWithUserIdForS3Videos(String videoId, String userId);

    List<Video> findAllVideos(String userId);

    Video findVideoById(String videoId, String userId);

    PresignedLinkResponse generatePresignedGetVideoUrl(String videoId, String userId);

    PresignedLinkResponse generatePresignedPutUrl(
            VideoFileUploadRequest videoFileUploadRequest,
            String userId);

    Video saveVideo(Video video, String userId);

    Video updateVideoById(Video video, String videoId, String userId);

    Video updateVideoDetailsById(VideoDetailsRequest videoDetailsRequest,
                                 String videoId,
                                 String userId);

    Video deleteVideoById(String videoId, String userId);

}
