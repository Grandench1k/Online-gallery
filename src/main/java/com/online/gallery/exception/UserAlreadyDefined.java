package com.online.gallery.exception;

public class UserAlreadyDefined extends RuntimeException {
    public UserAlreadyDefined(String message) {
        super(message);
    }
}
