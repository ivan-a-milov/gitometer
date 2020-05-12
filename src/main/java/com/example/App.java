package com.example;

import com.example.view.thymeleaf.ThymeleafView;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App
{
    public static void main( String[] args ) throws CommitSourceException, ViewException {
        String outputDir = "target/stub-render";
        Gitometer gitometer = new StubGitometer();
        Tree viewData = gitometer.tree("dummy-string");
        View view = new ThymeleafView(getTemplateEngine(), "Stub title");
        view.render(outputDir, true, viewData);
    }

    private static ITemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML");
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
