package com.example;

import com.example.source.jgit.JgitCommitSource;
import com.example.view.thymeleaf.ThymeleafView;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.IOException;

public class App
{
    private static Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws CommitSourceException, ViewException {
        final String repositoryPath = System.getProperty("repositoryPath");
        final String outputDir = System.getProperty("outputDir");
        final String revName1 = System.getProperty("revName1", "HEAD");
        final String revName2 = System.getProperty("revName2");

        StringBuilder startLogMessage = new StringBuilder();
        startLogMessage.append(
                "Start working on repository [" + repositoryPath
                        + "] output directory is [" + outputDir
                        + "] "
        );
        String titlePrefix = revName1;
        if (revName2 == null) {
            startLogMessage.append("revision " + revName1);
        } else {
            titlePrefix = revName1 + ".." + revName2;
            startLogMessage.append("revision range " + titlePrefix);
        }
        LOGGER.info(startLogMessage.toString());

        try {
            Repository repository = getRepository(repositoryPath);
            CommitSource commitSource = new JgitCommitSource(repository);
            Gitometer report = new GitometerImpl(commitSource);
            Tree viewData;
            if (revName2 == null) {
                viewData = report.tree(revName1);
            } else {
                viewData = report.tree(revName1, revName2);
            }
            View view = new ThymeleafView(getTemplateEngine(), titlePrefix);
            view.render(outputDir, true, viewData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.info("done :)");
    }

    private static ITemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private static Repository getRepository(String path) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        return repositoryBuilder.setGitDir(new File(path))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();
    }
}
