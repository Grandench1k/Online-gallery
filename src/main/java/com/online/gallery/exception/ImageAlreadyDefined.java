package com.online.gallery.exception;

public class ImageAlreadyDefined extends RuntimeException {
    public ImageAlreadyDefined(String message) {
        super(message);
    }
}
