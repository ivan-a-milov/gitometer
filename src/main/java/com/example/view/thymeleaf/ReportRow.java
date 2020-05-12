package com.example.view.thymeleaf;

import com.example.GitObject;
import com.example.GitObjectStats;

public class ReportRow {
    private final GitObject gitObject;
    private final String detailsHref;

    public ReportRow(GitObject gitObject, String detailsHref) {
        this.gitObject = gitObject;
        this.detailsHref = detailsHref;
    }

    public String name() {
        return gitObject.name();
    }

    public GitObjectStats stats() {
        return gitObject.stats();
    }

    public String detailsHref() {
        return detailsHref;
    }
}
