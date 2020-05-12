package com.example;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class GitometerImpl implements Gitometer {
    private final CommitSource commitSource;

    public GitometerImpl(CommitSource commitSource) {
        this.commitSource = commitSource;
    }

    @Override
    public Tree tree(String revName) throws CommitSourceException {
        Tree tree = buildInitialTree(commitSource.commitTreeStats(revName));
        List<String> hashes = commitSource.listCommitHashes(revName);
        return appendAll(tree, hashes);
    }

    @Override
    public Tree tree(String revName1, String revName2) throws CommitSourceException {
        Tree tree = buildInitialTree(commitSource.commitTreeStats(revName2));
        List<String> hashes = commitSource.listCommitHashes(revName1, revName2);
        return appendAll(tree, hashes);
    }

    private Tree appendAll(Tree tree, List<String> hashes) throws CommitSourceException {
        for (String hash : hashes) {
            Tree diffTree = buildDiffTree(commitSource.commitDiffStats(hash));
            tree = appendTree(tree, diffTree);
        }
        return tree;
    }

    static Tree appendTree(Tree tree, Tree diffTree) {
        Map<String, Tree> diffSubtrees = new HashMap<>();
        for (Tree dt : diffTree.subtrees()) {
            diffSubtrees.put(dt.name(), dt);
        }

        List<GitObjectStats> stats = new ArrayList<>();
        List<Tree> newSubtrees = new ArrayList<>();
        for (Tree t : tree.subtrees()) {
            Tree dt = diffSubtrees.get(t.name());
            if (dt != null) {
                t = appendTree(t, dt);
                diffSubtrees.remove(t.name());
            }
            stats.add(t.stats());
            newSubtrees.add(t);
        }
        for (Tree dt : diffSubtrees.values()) {
            stats.add(dt.stats());
        }

        Map<String, Blob> diffBlobs = new HashMap<>();
        for (Blob db : diffTree.blobs()) {
            diffBlobs.put(db.name(), db);
        }
        List<Blob> newBlobs = new ArrayList<>();
        for (Blob b : tree.blobs()) {
            Blob db = diffBlobs.get(b.name());
            if (db != null) {
                GitObjectStats newStats = new GitObjectStats(
                        b.stats().commits() + 1,
                        b.stats().linesAdded() + db.stats().linesAdded(),
                        b.stats().linesDeleted() + db.stats().linesDeleted(),
                        b.stats().totalLines()
                );
                b = new Blob(b.name(), newStats, b.isBinary());
                diffBlobs.remove(b.name());
            }
            stats.add(b.stats());
            newBlobs.add(b);
        }
        for (Blob db : diffBlobs.values()) {
            stats.add(db.stats());
        }

        int added = 0;
        int deleted = 0;
        for (GitObjectStats s : stats) {
            added += s.linesAdded();
            deleted += s.linesDeleted();
        }
        GitObjectStats newStats = new GitObjectStats(
                tree.stats().commits()+1,
                added, deleted,
                tree.stats().totalLines()
        );
        newSubtrees.sort(Comparator.comparing(GitObject::name));
        newBlobs.sort(Comparator.comparing(GitObject::name));
        return new Tree(tree.name(), newStats, newSubtrees, newBlobs);
    }


    static Tree buildInitialTree(List<Blob> blobs) {
        return buildTree(".", blobs, stats -> {
            int totalLines = 0;
            for (GitObjectStats s : stats) {
                totalLines += s.totalLines();
            }
            return new GitObjectStats(0,0,0, totalLines);
        });
    }

    static Tree buildDiffTree(List<Blob> blobs) {
        return buildTree(".", blobs, stats -> {
            int added = 0;
            int deleted = 0;
            for (GitObjectStats s : stats) {
                added += s.linesAdded();
                deleted += s.linesDeleted();
            }
            return new GitObjectStats(1, added, deleted, 0);
        });
    }


    private static Tree buildTree(String name, List<Blob> blobs, Function<List<GitObjectStats>, GitObjectStats> statReduce) {
        List<Blob> treeBlobs = new ArrayList<>();
        Map<String, List<Blob>> subtreeBlobs = new HashMap<>();
        List<GitObjectStats> stats = new ArrayList<>();

        for (Blob blob : blobs) {
            Path path = new File(blob.name()).toPath();
            if (path.getNameCount() == 1) {
                stats.add(blob.stats());
                treeBlobs.add(blob);
            } else {
                String subtreeName = path.getName(0).toString();
                List<Blob> stb = subtreeBlobs.computeIfAbsent(subtreeName, key -> new ArrayList<>());
                String blobName = path.subpath(1, path.getNameCount()).toString();
                stb.add(new Blob(blobName, blob.stats(), blob.isBinary()));
            }
        }

        List<Tree> subtrees = new ArrayList<>();
        for (Map.Entry<String, List<Blob>> entry : subtreeBlobs.entrySet()) {
            Tree subtree = buildTree(entry.getKey(), entry.getValue(), statReduce);
            stats.add(subtree.stats());
            subtrees.add(subtree);
        }

        GitObjectStats resultStats = statReduce.apply(stats);
        treeBlobs.sort(Comparator.comparing(GitObject::name));
        subtrees.sort(Comparator.comparing(GitObject::name));
        return new Tree(name, resultStats, subtrees, treeBlobs);
    }
}
