package com.example;

public interface View {
    void render(String path, boolean failIfExists, Tree tree) throws ViewException;
}
