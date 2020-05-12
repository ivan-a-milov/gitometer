package com.example;

public interface Gitometer {
    Tree tree(String revName) throws CommitSourceException;
    Tree tree(String revName1, String revName2) throws CommitSourceException;
}
