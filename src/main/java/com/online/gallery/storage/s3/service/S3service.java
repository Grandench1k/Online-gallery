package com.online.gallery.storage.s3.service;

public interface S3service {

    void putObject(String bucketName, String key, byte[] file);

    byte[] getObject(String bucketName, String key);

    void deleteObject(String bucketName, String key);
}
