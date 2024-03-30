package com.online.gallery.exception;

public class UserImageNotFound extends RuntimeException {
    public UserImageNotFound(String message) {
        super(message);
    }
}
