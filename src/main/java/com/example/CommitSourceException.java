package com.example;

public class CommitSourceException extends Exception {
    public CommitSourceException(String s, Exception e) {
        super(s, e);
    }

    public CommitSourceException(String s) {
        super(s);
    }
}
