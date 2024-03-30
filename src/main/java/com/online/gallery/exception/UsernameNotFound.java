package com.online.gallery.exception;

public class UsernameNotFound extends RuntimeException {
    public UsernameNotFound(String message) {
        super(message);
    }
}
