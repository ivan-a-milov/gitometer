package com.example;

import java.util.List;
import java.util.Objects;

public class Tree extends GitObject {
    private final List<Tree> subtrees;
    private final List<Blob> blobs;

    public Tree(String name, GitObjectStats stats, List<Tree> subtrees, List<Blob> blobs) {
        super(name, stats);
        this.subtrees = subtrees;
        this.blobs = blobs;
    }

    public List<Tree> subtrees() {
        return subtrees;
    }

    public List<Blob> blobs() {
        return blobs;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other instanceof Tree) {
            Tree tree = (Tree) other;
            return equalsCommon(tree)
                    && Objects.equals(blobs(), tree.blobs())
                    && Objects.equals(subtrees(), tree.subtrees());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Tree(name=" + name()
                + ", stats=" + stats()
                + ", subtrees=" + subtrees()
                + ", blobs=" + blobs()
                + ")";
    }
}
