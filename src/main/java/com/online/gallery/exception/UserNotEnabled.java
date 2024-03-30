package com.online.gallery.exception;

public class UserNotEnabled extends RuntimeException {
    public UserNotEnabled(String message) {
        super(message);
    }
}
