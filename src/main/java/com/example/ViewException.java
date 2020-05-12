package com.example;

public class ViewException extends Exception {
    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewException(String message) {
        super(message);
    }
}
