package com.example.view.thymeleaf;

import com.example.GitObject;
import com.example.Tree;
import com.example.View;
import com.example.ViewException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class ThymeleafView implements View {
    private static Logger LOGGER = LoggerFactory.getLogger(ThymeleafView.class);
    private final static String templateName = "dir-report.html";

    private final ITemplateEngine templateEngine;
    private final String titlePrefix;

    public ThymeleafView(ITemplateEngine templateEngine, String titlePrefix) {
        this.templateEngine = templateEngine;
        this.titlePrefix = titlePrefix;
    }

    @Override
    public void render(String path, boolean failIfExists, Tree tree) throws ViewException {
        cleanupPath(path, failIfExists);
        renderTree(Collections.singletonList(path), tree);
        LOGGER.info("Copying css file");
        copyStyleFile(path);
    }

    private void renderTree(List<String> path, Tree tree) throws ViewException {
        String dirName = String.join("/subtrees/", path);
        LOGGER.info("Rendering path " + String.join("/", path));

        File baseDir = new File(dirName);
        if (!baseDir.mkdirs()) {
            throw new ViewException("Failed to create directory " + path);
        }

        StringWriter content = renderPage(path.subList(1, path.size()), tree);

        String fileName = dirName + "/index.html";
        try {
            Files.write(Paths.get(fileName), content.toString().getBytes());
        } catch (IOException e) {
            throw new ViewException("Failed to write file " + fileName, e);
        }

        for (Tree subtree : tree.subtrees()) {
            List<String> subtreePath = new ArrayList<>(path);
            subtreePath.add(subtree.name());
            renderTree(subtreePath, subtree);
        }
    }

    private StringWriter renderPage(List<String> pagePath, Tree tree) {
        List<GitObject> rows = getTreeRows(tree);
        List<ReportRow> reportRows = new ArrayList<>();
        for (GitObject row : rows) {
            // ну и костыль !!!
            String detailsHref = "";
            if (row instanceof Tree) {
                detailsHref = "subtrees/" + row.name() + "/index.html";
            }
            reportRows.add(new ReportRow(row, detailsHref));
        }

        Context context = new Context();
        context.setVariable("rows", reportRows);
        context.setVariable("title", titlePrefix + ". " + String.join("/", pagePath));
        context.setVariable("tree", tree);

        // template should know nothing about directories structure
        if (pagePath.size() != 0) {
            context.setVariable("parentHref", "../../index.html");
        }
        String styleRel = parentPathUpTo(pagePath.size()) + "style.css";
        context.setVariable("styleRel", styleRel);

        LinkedHashMap<String, String> breadcrumbs = new LinkedHashMap<>();
        for (int i=1; i<pagePath.size(); i++) {
            breadcrumbs.put(pagePath.get(i-1), parentPathUpTo(pagePath.size()-i) + "index.html");
        }
        context.setVariable("breadcrumbs", breadcrumbs);

        StringWriter stringWriter = new StringWriter();
        templateEngine.process(templateName, context, stringWriter);
        return stringWriter;
    }

    private static String parentPathUpTo(int n) {
        return String.join("", Collections.nCopies(n, "../../"));
    }

    private static void cleanupPath(String path, boolean failIfExists) throws ViewException {
        File baseDir = new File(path);
        if (baseDir.exists()) {
            if (failIfExists) {
                throw new ViewException("Directory already exists " + path);
            }
            try {
                LOGGER.info("Removing directory " + path);
                Stream<Path> files = Files.walk(Paths.get(path));
                files.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                files.close();
            } catch (IOException ex) {
                throw new ViewException("Failed to remove directory " + path, ex);
            }
        }
    }

    private static void copyStyleFile(String path) throws ViewException {
        Path dest = Paths.get(path + "/style.css");
        try {
            InputStream src = ThymeleafView.class.getClassLoader().getResource("style.css").openStream();
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ViewException("Failed to copy CSS style to " + dest, e);
        }
    }

    private static List<GitObject> getTreeRows(Tree tree) {
        List<GitObject> rows = new ArrayList<>(tree.subtrees()); // TODO is defensive copying needed ?
        rows.sort(Comparator.comparing(GitObject::name));

        List<GitObject> blobs = new ArrayList<>(tree.blobs()); // TODO is defensive copying needed ?
        blobs.sort(Comparator.comparing(GitObject::name));

        rows.addAll(blobs);
        return rows;
    }
}
