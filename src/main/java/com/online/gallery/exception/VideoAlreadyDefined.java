package com.online.gallery.exception;

public class VideoAlreadyDefined extends RuntimeException {
    public VideoAlreadyDefined(String message) {
        super(message);
    }
}
