package com.example.source.jgit;

import com.example.Blob;
import com.example.CommitSource;
import com.example.CommitSourceException;
import com.example.GitObjectStats;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JgitCommitSource implements CommitSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(JgitCommitSource.class);
    private final Repository repository;

    public JgitCommitSource(Repository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> listCommitHashes(String revName) throws CommitSourceException {
        LOGGER.info("Log for revision " + revName);
        List<String> hashes;
        try (Git git = new Git(repository)) {
            ObjectId revId = resolveRevision(revName);
            LogCommand cmd = git.log().add(revId);
            hashes = listLogHashes(cmd);
        } catch (GitAPIException | IOException e) {
            throw new CommitSourceException("Failed to list commits for revision " + revName, e);
        }
        return hashes;
    }

    @Override
    public List<String> listCommitHashes(String revName1, String revName2) throws CommitSourceException {
        LOGGER.info("Log for revision range " + revName1 + ".." + revName2);
        List<String> hashes;
        try (Git git = new Git(repository)) {
            ObjectId revId1 = resolveRevision(revName1);
            ObjectId revId2 = resolveRevision(revName2);
            LogCommand cmd = git.log().addRange(revId1, revId2);
            hashes = listLogHashes(cmd);
        } catch (GitAPIException | IOException e) {
            throw new CommitSourceException(
                    "Failed to list commits for revision range" + revName1 + ".." + revName2, e
            );
        }
        return hashes;
    }

    private static List<String> listLogHashes(LogCommand cmd) throws GitAPIException {
        List<String> hashes = new ArrayList<>();
        Iterable<RevCommit> commits = cmd.call();
        for (RevCommit commit : commits) {
            hashes.add(commit.getName());
        }
        return hashes;
    }

    // Equivalent to "git show --stat [revName]"
    @Override
    public List<Blob> commitDiffStats(String revName) throws CommitSourceException {
        LOGGER.info("Show stat revision " + revName);
        List<Blob> result = new ArrayList<>();
        try(RevWalk walk = new RevWalk(repository)) {
            ObjectId refId = resolveRevision(revName);
            RevCommit commit = walk.parseCommit(refId);
            if (commit.getParentCount() > 1) {
                // TODO how should be counted merge commits???
                LOGGER.info("Merge commit is ignored. Revision " + revName);
                return result;
            } else if (commit.getParentCount() == 0) {
                List<FileStats> lineCounts = countCommitLines(revName);
                for (FileStats fs : lineCounts) {
                    GitObjectStats stats = new GitObjectStats(1, fs.lines(), 0, 0);
                    result.add(new Blob(fs.name(), stats, fs.isBinary()));
                }
                return result;
            }
            RevCommit parent = walk.parseCommit(commit.getParent(0).getId());

            DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
            df.setRepository(repository);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

            for (DiffEntry diff : diffs) {
                FileHeader fileHeader = df.toFileHeader(diff);
                String path = fileHeader.getOldPath();
                if (fileHeader.getChangeType() == DiffEntry.ChangeType.ADD) {
                    path = fileHeader.getNewPath();
                }

                if (fileHeader.getPatchType() != FileHeader.PatchType.UNIFIED) {
                    // TODO how should stat be counted for binary files ??
                    LOGGER.info("Binary file " + path + " is ignored");
                    continue;
                }

                int linesAdded = 0;
                int linesDeleted = 0;
                for (Edit edit : df.toFileHeader(diff).toEditList()) {
                    linesAdded += edit.getEndB() - edit.getBeginB();
                    linesDeleted += edit.getEndA() - edit.getBeginA();
                }


                result.add(new Blob(path, new GitObjectStats(1, linesAdded, linesDeleted, 0)));
            }
        } catch (IOException e) {
            throw new CommitSourceException("Failed to count diff stats for revision " + revName, e);
        }
        return result;
    }

    @Override
    public List<Blob> commitTreeStats(String revName)  throws CommitSourceException {
        LOGGER.info("Count total lines for revision " + revName);
        List<Blob> result = new ArrayList<>();
        List<FileStats> lineCounts = countCommitLines(revName);
        for (FileStats fs : lineCounts) {
            GitObjectStats stats = new GitObjectStats(0, 0, 0, fs.lines());
            result.add(new Blob(fs.name(), stats, fs.isBinary()));
        }
        return result;
    }

    private ObjectId resolveRevision(String revName) throws IOException, CommitSourceException {
        ObjectId id = repository.resolve(revName);
        if (id == null) {
            throw new CommitSourceException("No such revision: " + revName);
        }
        return id;
    }

    private static class FileStats {
        private final String name;
        private final int lines;
        private final boolean isBinary;

        private FileStats(String name, int lines, boolean isBinary) {
            this.name = name;
            this.lines = lines;
            this.isBinary = isBinary;
        }

        String name() {
            return name;
        }

        int lines() {
            return lines;
        }

        boolean isBinary() {
            return isBinary;
        }
    }

    private Set<ObjectId> getSubmoduleIds() throws IOException {
        Set<ObjectId> ids = new HashSet<>();
        try (SubmoduleWalk walk = SubmoduleWalk.forIndex(repository)) {
            do {
                ids.add(walk.getObjectId());
            } while (walk.next());
        } catch (NoWorkTreeException e) { // one more dirty hack :(
        }
        return ids;
    }

    private List<FileStats> countCommitLines(String revName) throws CommitSourceException {
        List<FileStats> result = new ArrayList<>();
        try (RevWalk walk = new RevWalk(repository)) {
            ObjectId refId = repository.resolve(revName);
            Set<ObjectId> submoduleIds = getSubmoduleIds();
            if (refId == null) {
                throw new CommitSourceException("No such revision: " + revName);
            }
            RevCommit commit = walk.parseCommit(refId);

            RevTree tree = commit.getTree();
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                ObjectLoader loader;
                while (treeWalk.next()) {
                    ObjectId objId = treeWalk.getObjectId(0);
                    if (submoduleIds.contains(objId)) {
                        continue;
                    }
                    loader = repository.open(objId);

                    byte[] objectBytes = loader.getBytes();
                    boolean isBinary = RawText.isBinary(objectBytes);
                    int lines = 0;
                    if (!isBinary) {
                        for (byte b : objectBytes) {
                            if (b == '\n') {
                                lines++;
                            }
                        }
                    } else {
                        LOGGER.info("Binary file " + treeWalk.getPathString() + " is ignored");
                    }
                    result.add(new FileStats(treeWalk.getPathString(), lines, isBinary));
                }
            }
            walk.dispose();
        } catch (IOException e) {
            throw new CommitSourceException("FAiled to count total lines for revision " + revName, e);
        }
        return result;
    }
}
