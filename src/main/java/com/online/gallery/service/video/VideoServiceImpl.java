package com.online.gallery.service.video;

import com.online.gallery.exception.InvalidFileFormatException;
import com.online.gallery.exception.VideoDuplicationException;
import com.online.gallery.exception.VideoNotFoundException;
import com.online.gallery.model.media.Video;
import com.online.gallery.repository.VideoRepository;
import com.online.gallery.storage.s3.service.S3service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final S3service s3service;
    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    public VideoServiceImpl(VideoRepository videoRepository, S3service s3service) {
        this.videoRepository = videoRepository;
        this.s3service = s3service;
    }

    public String generateLinkWithUserIdForS3Videos(String userId) {
        return "videos/" + userId + "/";
    }

    public List<Video> findAllVideos(String userId) {
        List<Video> allVideos = videoRepository.findAllByUserId(userId);
        if (allVideos.isEmpty()) {
            throw new VideoNotFoundException("videos not found.");
        }
        return allVideos;
    }

    public byte[] findVideoById(String videoId, String userId) {
        Video video = videoRepository.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("video with this id Not Found."));
        return s3service.getObject(bucketName, generateLinkWithUserIdForS3Videos(userId) + video.getUri());
    }


    @CachePut(cacheNames = "videoCache")
    public Video saveVideo(Video videoToSave, MultipartFile videoFile, String userId) throws IOException {
        if (videoRepository.findByNameAndUserId(videoToSave.getName(), userId).isPresent()) {
            throw new VideoDuplicationException("video with this name is already defined.");
        }
        String fileFormat = checkAndGetFileFormat(videoFile);
        String id = new ObjectId().toString();
        String nameOfNewFile = id + fileFormat;
        s3service.putObject(bucketName, generateLinkWithUserIdForS3Videos(userId) + nameOfNewFile, videoFile.getBytes());
        videoToSave.setUri(nameOfNewFile);
        videoToSave.setId(id);
        videoToSave.setUserId(userId);
        videoRepository.save(videoToSave);
        return videoToSave;
    }

    private String checkAndGetFileFormat(MultipartFile videoFile) {
        String originalFilename = videoFile.getOriginalFilename();
        String fileFormat = Objects.requireNonNull(originalFilename).substring(originalFilename.indexOf("."));
        if (!fileFormat.contentEquals(".mp4")
                && !fileFormat.contentEquals(".mpeg")
                && !fileFormat.contentEquals(".ogg")
        ) {
            throw new InvalidFileFormatException("incorrect video format, please send video with .mp4, .mpeg and .ogg formats.");
        }
        return fileFormat;
    }

    @CachePut(cacheNames = "videoCache")
    public Video updateVideoById(String videoId, Video video, String userId) {
        Video videoToUpdate = videoRepository.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("video with this id not found."));
        String videoName = video.getName();
        if (videoRepository.findByNameAndUserId(videoName, userId).isPresent()) {
            throw new VideoDuplicationException("video with this name is already defined.");
        }
        videoToUpdate.setName(videoName);
        videoRepository.save(videoToUpdate);
        return videoToUpdate;
    }

    @CacheEvict(cacheNames = "videoCache", key = "#videoId")
    public Video deleteVideoById(String videoId, String userId) {
        Video videoToDelete = videoRepository.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("video with this id not found."));
        s3service.deleteObject(bucketName, videoToDelete.getUri());
        videoRepository.delete(videoToDelete);
        return videoToDelete;
    }
}
