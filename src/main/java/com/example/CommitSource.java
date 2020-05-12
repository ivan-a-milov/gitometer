package com.example;

import java.util.List;

public interface CommitSource {
    List<String> listCommitHashes(String revName) throws CommitSourceException;
    List<String> listCommitHashes(String revName1, String revName2) throws CommitSourceException;
    List<Blob> commitDiffStats(String revName) throws CommitSourceException;
    List<Blob> commitTreeStats(String revName)  throws CommitSourceException;
}
