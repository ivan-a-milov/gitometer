package com.example;

public class GitObjectStats {
    private final int commits;
    private final int linesAdded;
    private final int linesDeleted;
    private final int totalLines;

    public GitObjectStats(int commits, int linesAdded, int linesDeleted, int totalLines) {
        this.commits = commits;
        this.linesAdded = linesAdded;
        this.linesDeleted = linesDeleted;
        this.totalLines = totalLines;
    }

    public int commits() {
        return commits;
    }

    public int linesAdded() {
        return linesAdded;
    }

    public int linesDeleted() {
        return linesDeleted;
    }

    public int totalLines() {
        return totalLines;
    }

    public int linesModified() {
        return linesAdded() + linesDeleted();
    }

    public float modificationRate() {
        // If content of file was deleted, it changes modification rate of parent tree
        if (totalLines() == 0) {
            return 0.0f;
        }
        return ((float) linesModified())/totalLines();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other instanceof GitObjectStats) {
            GitObjectStats stats = (GitObjectStats) other;
            return commits() == stats.commits() && linesAdded() == stats.linesAdded()
                    && linesDeleted() == stats.linesDeleted() && totalLines() == stats.totalLines();
        }
        return false;
    }

    @Override
    public String toString() {
        return "GitObjectStats("
                + "commits=" + commits()
                + ", linesAdded=" + linesAdded()
                + ", linesDeleted=" + linesDeleted()
                + ", totalLines=" + totalLines()
                + ")";
    }
}
