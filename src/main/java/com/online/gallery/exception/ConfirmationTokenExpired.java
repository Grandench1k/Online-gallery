package com.online.gallery.exception;

public class ConfirmationTokenExpired extends RuntimeException {
    public ConfirmationTokenExpired(String message) {
        super(message);
    }
}
