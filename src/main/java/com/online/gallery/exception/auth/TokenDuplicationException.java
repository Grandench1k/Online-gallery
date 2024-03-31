package com.online.gallery.exception.auth;

public class TokenDuplicationException extends RuntimeException {
    public TokenDuplicationException(String message) {
        super(message);
    }
}
