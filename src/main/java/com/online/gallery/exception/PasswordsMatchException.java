package com.online.gallery.exception;

public class PasswordsMatchException extends RuntimeException {
    public PasswordsMatchException(String message) {
        super(message);
    }
}

