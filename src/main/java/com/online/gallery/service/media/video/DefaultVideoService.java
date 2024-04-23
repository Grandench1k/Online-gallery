package com.online.gallery.service.media.video;

import com.online.gallery.exception.file.InvalidFileFormatException;
import com.online.gallery.exception.file.InvalidFilenameException;
import com.online.gallery.exception.media.video.VideoDuplicationException;
import com.online.gallery.exception.media.video.VideoNotFoundException;
import com.online.gallery.model.media.Video;
import com.online.gallery.repository.media.VideoRepository;
import com.online.gallery.storage.s3.service.S3service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
public class DefaultVideoService implements VideoService {
    private final VideoRepository videoRepository;
    private final S3service s3service;
    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;

    public DefaultVideoService(VideoRepository videoRepository, S3service s3service) {
        this.videoRepository = videoRepository;
        this.s3service = s3service;
    }

    public String generateLinkWithUserIdForS3Videos(String userId) {
        return "videos/" + userId + "/";
    }

    @Cacheable(cacheNames = "videoCache", key = "'list-' + #userId")
    public List<Video> findAllVideos(String userId) {
        List<Video> allVideos = videoRepository.findAllByUserId(userId);
        if (allVideos.isEmpty()) {
            throw new VideoNotFoundException("videos not found for user " + userId);
        }
        return allVideos;
    }

    public byte[] findVideoById(String videoId, String userId) {
        Video video = videoRepository.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("video with ID " + videoId + " not found"));
        return s3service.getObject(bucketName,
                generateLinkWithUserIdForS3Videos(userId) + video.getFilePath());
    }

    public String getFileFormat(MultipartFile videoFile) {
        String originalFilename = videoFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFilenameException("invalid filename");
        }
        String fileFormat = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        Set<String> allowedFormats = new HashSet<>(Arrays.asList(".mp4", ".mpeg", ".ogg"));
        if (!allowedFormats.contains(fileFormat)) {
            throw new InvalidFileFormatException(
                    "incorrect video format, please send video with .mp4, .mpeg, or .ogg formats");
        }
        return fileFormat;
    }

    @CachePut(cacheNames = "videoCache", key = "'list-' + #userId")
    public Video saveVideo(Video videoToSave, MultipartFile videoFile, String userId) throws IOException {
        videoRepository.findByNameAndUserId(videoToSave.getName(), userId)
                .ifPresent(s -> {
                    throw new VideoDuplicationException(
                            "video with this name is already defined for user " + userId);
                });

        String fileFormat = getFileFormat(videoFile);
        String id = new ObjectId().toString();
        String nameOfNewFile = id + fileFormat;
        s3service.putObject(bucketName,
                generateLinkWithUserIdForS3Videos(userId) + nameOfNewFile,
                videoFile.getBytes());
        videoToSave.setFilePath(nameOfNewFile);
        videoToSave.setId(id);
        videoToSave.setUserId(userId);
        return videoRepository.save(videoToSave);
    }

    @CachePut(cacheNames = "videoCache", key = "'video-' + #videoId")
    public Video updateVideoById(String videoId, Video video, String userId) {
        Video videoToUpdate = videoRepository.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("video with ID " + videoId + " not found"));

        videoRepository.findByNameAndUserId(video.getName(), userId)
                .ifPresent(s -> {
                    throw new VideoDuplicationException(
                            "video with this name is already defined for user " + userId);
                });

        videoToUpdate.setName(video.getName());
        return videoRepository.save(videoToUpdate);
    }

    @CacheEvict(cacheNames = "videoCache", allEntries = true)
    public Video deleteVideoById(String videoId, String userId) {
        Video videoToDelete = videoRepository.findByIdAndUserId(videoId, userId)
                .orElseThrow(() -> new VideoNotFoundException("video with ID " + videoId + " not found"));
        s3service.deleteObject(
                bucketName,
                generateLinkWithUserIdForS3Videos(userId) + videoToDelete.getFilePath());
        videoRepository.delete(videoToDelete);
        return videoToDelete;
    }
}