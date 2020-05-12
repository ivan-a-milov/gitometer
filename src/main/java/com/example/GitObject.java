package com.example;

import java.util.Objects;

abstract public class GitObject {
    private final String name;
    private final GitObjectStats stats;

    public GitObject(String name, GitObjectStats stats) {
        this.name = name;
        this.stats = stats;
    }

    public String name() {
        return name;
    }

    public GitObjectStats stats() {
        return stats;
    }

    final protected boolean equalsCommon(GitObject other) {
        if (other == null) {
            return false;
        }
        return Objects.equals(name(), other.name()) && Objects.equals(stats(), other.stats());
    }
}
