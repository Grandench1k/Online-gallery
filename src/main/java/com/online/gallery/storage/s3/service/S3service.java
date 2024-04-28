package com.online.gallery.storage.s3.service;

public interface S3service {
    String generatePresignedGetObjectUrl(String bucketName,
                                         String objectKey,
                                         long expirationDurationSeconds);

    String generatePresignedPutObjectUrl(String bucketName,
                                         String objectKey,
                                         long expirationDurationSeconds);

    void deleteObject(String bucketName, String key);
}