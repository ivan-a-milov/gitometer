package com.example.stub;

import com.example.Blob;
import com.example.GitObjectStats;
import com.example.Gitometer;
import com.example.Tree;

import java.util.ArrayList;
import java.util.List;

public class StubGitometer implements Gitometer {
    @Override
    public Tree tree(String revName) {
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new Blob("pom.xml", new GitObjectStats(1, 2, 3, 4)));
        blobs.add(new Blob("clean-exec.sh", new GitObjectStats(5, 6, 7, 8)));

        List<Tree> subtrees = new ArrayList<>();
        subtrees.add(srcSubtree());

        GitObjectStats stats = new GitObjectStats(12, 34, 56, 78);
        return new Tree(".", stats, subtrees, blobs);
    }

    @Override
    public Tree tree(String revName1, String revName2) {
        throw new RuntimeException("Not implemented yet");
    }

    private static Tree srcSubtree() {
        List<Blob> blobs = new ArrayList<>();

        List<Tree> subtrees = new ArrayList<>();
        subtrees.add(srcJavaSubtree());
        subtrees.add(srcResourcesSubtree());

        GitObjectStats stats = new GitObjectStats(112, 234, 356, 478);
        return new Tree("src", stats, subtrees, blobs);
    }

    private static Tree srcResourcesSubtree() {
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new Blob("dir-report.html", new GitObjectStats(300, 401, 502, 666)));
        blobs.add(new Blob("icon.ico", new GitObjectStats(44, 0, 0, 0)));
        blobs.add(new Blob("stub.html", new GitObjectStats(2, 12, 6, 92)));

        List<Tree> subtrees = new ArrayList<>();

        GitObjectStats stats = new GitObjectStats(999, 888, 777, 666);
        return new Tree("resources", stats, subtrees, blobs);
    }

    private static Tree srcJavaSubtree() {
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new Blob("StubRepositoryInfo.java", new GitObjectStats(4005, 3006, 2007, 1008)));
        blobs.add(new Blob("Main.java", new GitObjectStats(40, 30, 20, 10)));

        List<Tree> subtrees = new ArrayList<>();
        subtrees.add(srcJavaComSubtree());

        GitObjectStats stats = new GitObjectStats(31, 415, 92, 65);
        return new Tree("java", stats, subtrees, blobs);
    }

    private static Tree srcJavaComSubtree() {
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new Blob("One.java", new GitObjectStats(1, 1, 1, 1)));
        blobs.add(new Blob("Two.java", new GitObjectStats(2, 2, 2, 2)));

        List<Tree> subtrees = new ArrayList<>();

        GitObjectStats stats = new GitObjectStats(6, 6, 6, 6);
        return new Tree("com", stats, subtrees, blobs);
    }
}
