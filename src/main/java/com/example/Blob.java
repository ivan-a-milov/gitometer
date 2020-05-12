package com.example;

public class Blob extends GitObject {
    private final boolean isBinary;

    public Blob(String name,GitObjectStats stats, boolean isBinary) {
        super(name, stats);
        this.isBinary = isBinary;
    }

    public Blob(String name, GitObjectStats stats) {
        this(name, stats, false);
    }

    public boolean isBinary() {
        return isBinary;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other instanceof Blob) {
            Blob blob = (Blob) other;
            return equalsCommon(blob) && isBinary() == blob.isBinary();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Blob(name=" + name()
                + ", stats=" + stats()
                + ", " + (isBinary() ? "BINARY" : "TEXT")
                + ")";
    }
}
