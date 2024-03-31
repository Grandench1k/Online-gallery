package com.online.gallery.exception.user;

public class PasswordsMatchException extends RuntimeException {
    public PasswordsMatchException(String message) {
        super(message);
    }
}

