package com.online.gallery.exception;

public class PasswordResetTokenAlreadyDefined extends RuntimeException {
    public PasswordResetTokenAlreadyDefined(String message) {
        super(message);
    }
}
