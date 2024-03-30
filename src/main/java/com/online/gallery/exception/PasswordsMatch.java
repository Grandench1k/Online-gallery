package com.online.gallery.exception;

public class PasswordsMatch extends RuntimeException {
    public PasswordsMatch(String message) {
        super(message);
    }
}

