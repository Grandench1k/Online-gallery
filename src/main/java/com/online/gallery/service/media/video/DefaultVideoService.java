package com.online.gallery.service.media.video;

import com.online.gallery.dto.request.VideoDetailsRequest;
import com.online.gallery.dto.request.VideoFileUploadRequest;
import com.online.gallery.dto.response.PresignedLinkResponse;
import com.online.gallery.entity.media.Video;
import com.online.gallery.exception.media.image.ImageDuplicationException;
import com.online.gallery.exception.media.video.VideoDuplicationException;
import com.online.gallery.exception.media.video.VideoNotFoundException;
import com.online.gallery.exception.user.UserAccessDeniedException;
import com.online.gallery.repository.media.VideoRepo;
import com.online.gallery.storage.s3.service.S3service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Service
public class DefaultVideoService implements VideoService {
    private final VideoRepo videoRepo;
    private final S3service s3service;
    private final String notFoundMessage = "video with this ID not found";
    private final String duplicationMessage = "video with this name is already defined";

    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    @Value("${aws.s3.video.get.expiration.s}")
    private long videoGetExpiration;

    @Value("${aws.s3.video.put.expiration.s}")
    private long videoPutExpiration;

    public DefaultVideoService(VideoRepo videoRepo, S3service s3service) {
        this.videoRepo = videoRepo;
        this.s3service = s3service;
    }

    public String generateLinkWithUserIdForS3Videos(String videoId, String userId) {
        return "videos/" + userId + "/" + videoId + "/";
    }

    @Cacheable(cacheNames = "videosCache", key = "'videoList-' + #userId")
    public List<Video> findAllVideos(String userId) {
        List<Video> allVideos = videoRepo.findAllByUserId(userId);
        if (allVideos.isEmpty()) {
            throw new VideoNotFoundException("no videos found");
        }
        return allVideos;
    }

    @Cacheable(cacheNames = "videosCache", key = "'video-' + #videoId")
    public Video findVideoById(String videoId, String userId) {
        return videoRepo.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException(notFoundMessage));
    }

    public PresignedLinkResponse generatePresignedGetVideoUrl(String videoId, String userId) {
        Video video = videoRepo.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException(notFoundMessage));
        return new PresignedLinkResponse(
                videoGetExpiration,
                s3service.generatePresignedGetObjectUrl(
                        bucketName, video.getFilePath(), videoGetExpiration));
    }

    public PresignedLinkResponse generatePresignedPutUrl(
            VideoFileUploadRequest videoFileUploadRequest, String userId) {
        String videoName = videoFileUploadRequest.getName();
        String id = videoFileUploadRequest.getId();
        if (videoRepo.existsByIdAndUserId(id, userId)) {
            throw new ImageDuplicationException(duplicationMessage);
        }
        String filePath = generateLinkWithUserIdForS3Videos(userId, id) + videoName;
        return new PresignedLinkResponse(
                videoPutExpiration,
                s3service.generatePresignedPutObjectUrl(
                        bucketName, filePath, videoPutExpiration));
    }

    @CacheEvict(cacheNames = "videosCache", key = "'videoList-' + #userId")
    public Video saveVideo(Video videoToSave, String userId) {
        String videoName = videoToSave.getName();
        String videoId = videoToSave.getId();
        if (videoRepo.existsByIdAndUserId(videoId, userId)) {
            throw new ImageDuplicationException(duplicationMessage);
        }
        String filePath = generateLinkWithUserIdForS3Videos(userId, videoId) + videoName;
        Video video = new Video(videoId, videoName, filePath, userId);
        return videoRepo.save(video);
    }

    @CacheEvict(cacheNames = "videosCache", allEntries = true)
    public Video updateVideoById(Video newVideo, String videoId, String userId) {
        if (newVideo.getUserId() != null && !newVideo.getUserId().equals(userId)) {
            throw new UserAccessDeniedException("you don't have access to another user");
        }
        Video videoToUpdate = videoRepo.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException(notFoundMessage));
        if (!videoId.equals(newVideo.getId())) {
            throw new VideoNotFoundException("video with this ID in request body not found");
        }
        videoRepo.findByNameAndUserId(newVideo.getName(), userId)
                .ifPresent(s -> {
                    throw new VideoDuplicationException(
                            duplicationMessage);
                });
        videoRepo.delete(videoToUpdate);
        return videoRepo.save(videoToUpdate);
    }

    @CacheEvict(cacheNames = "videosCache", allEntries = true)
    public Video updateVideoDetailsById(VideoDetailsRequest newVideoDetails, String videoId, String userId) {
        Video videoToUpdate = videoRepo.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException(notFoundMessage));
        String videoName = newVideoDetails.getName();
        videoRepo.findByNameAndUserId(videoName, userId)
                .ifPresent(s -> {
                    throw new VideoDuplicationException(
                            duplicationMessage);
                });
        videoToUpdate.setName(videoName);
        return videoRepo.save(videoToUpdate);
    }

    @CacheEvict(cacheNames = "videosCache", allEntries = true)
    public Video deleteVideoById(String videoId, String userId) {
        Video videoToDelete = videoRepo.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException(notFoundMessage));
        s3service.deleteObject(bucketName, videoToDelete.getFilePath());
        videoRepo.delete(videoToDelete);
        return videoToDelete;
    }
}

