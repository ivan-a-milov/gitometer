package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GitometerImplTest extends TestCase {
    public GitometerImplTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( GitometerImplTest.class );
    }

    public void testBuildInitialTree() {
        Tree actual = GitometerImpl.buildInitialTree(initialTree());
        Tree expected = expectedInitialTree();
        assertEquals(expected, actual);
    }

    public void testBuildDiffTree() {
        Tree actual = GitometerImpl.buildDiffTree(diffTree());
        Tree expected = expectedDiffTree();
        assertEquals(expected, actual);
    }

    public void testAppend() {
        Tree actual = GitometerImpl.appendTree(treeToAppendTo(), treeToBeAppended());
        Tree expected = expectedAppendedTrees();
        assertEquals(expected, actual);
    }

    private static Tree treeToAppendTo() {
        List<Blob> mainBlobs = new ArrayList<>();
        mainBlobs.add(new Blob("Bar.java", new GitObjectStats(2,5,17,32)));
        mainBlobs.add(new Blob("Foo.java", new GitObjectStats(1,3,13,16)));
        Tree mainTree = new Tree(
                "main", new GitObjectStats(2, 8, 30, 48),
                new ArrayList<>(), mainBlobs
        );
        List<Blob> testBlobs = new ArrayList<>();
        testBlobs.add(new Blob("BarTest.java", new GitObjectStats(7,11,21,77)));
        testBlobs.add(new Blob("FooTest.java", new GitObjectStats(3,7,19,64)));
        Tree testTree = new Tree(
                "test", new GitObjectStats(7, 18, 40, 141),
                new ArrayList<>(), testBlobs
        );
        Blob garbageBlob = new Blob("forgottenGarbage.txt", new GitObjectStats(1,8,16,31));
        return new Tree(
                ".", new GitObjectStats(7, 34, 86, 210),
                Arrays.asList(mainTree, testTree), Collections.singletonList(garbageBlob)
        );
    }

    private static Tree treeToBeAppended() {
        List<Blob> mainBlobs = new ArrayList<>();
        mainBlobs.add(new Blob("Baz.java", new GitObjectStats(1,0,33,77)));
        mainBlobs.add(new Blob("Foo.java", new GitObjectStats(1,3,13,16)));
        Tree mainTree = new Tree(
                "main", new GitObjectStats(1, 3, 46, 0),
                new ArrayList<>(), mainBlobs
        );
        List<Blob> targetBlobs = Collections.singletonList(
                new Blob("garbage.txt", new GitObjectStats(1,0,13,0))
        );
        Tree targetTree = new Tree(
                "target", new GitObjectStats(1, 0, 13, 0),
                new ArrayList<>(), targetBlobs
        );
        List<Blob> testBlobs = Collections.singletonList(
                new Blob("FooTest.java", new GitObjectStats(1,8,3,99))
        );
        Tree testTree = new Tree(
                "test", new GitObjectStats(1, 8, 3, 0),
                new ArrayList<>(), testBlobs
        );
        return new Tree(
                ".", new GitObjectStats(1, 11, 62, 0),
                Arrays.asList(mainTree, targetTree, testTree), new ArrayList<>()
        );
    }

    private static Tree expectedAppendedTrees() {
        List<Blob> mainBlobs = new ArrayList<>();
        mainBlobs.add(new Blob("Bar.java", new GitObjectStats(2,5,17,32)));
        mainBlobs.add(new Blob("Foo.java", new GitObjectStats(2,6,26,16)));
        Tree mainTree = new Tree(
                "main", new GitObjectStats(3, 11, 76, 48),
                new ArrayList<>(), mainBlobs
        );
        List<Blob> testBlobs = new ArrayList<>();
        testBlobs.add(new Blob("BarTest.java", new GitObjectStats(7,11,21,77)));
        testBlobs.add(new Blob("FooTest.java", new GitObjectStats(4,15,22,64)));
        Tree testTree = new Tree(
                "test", new GitObjectStats(8, 26, 43, 141),
                new ArrayList<>(), testBlobs
        );
        Blob garbageBlob = new Blob("forgottenGarbage.txt", new GitObjectStats(1,8,16,31));
        return new Tree(
                ".", new GitObjectStats(8, 45, 148, 210),
                Arrays.asList(mainTree, testTree), Collections.singletonList(garbageBlob)
        );
    }

    private static Tree expectedDiffTree() {
        List<Blob> mainBlobs = new ArrayList<>();
        mainBlobs.add(new Blob("App.java", new GitObjectStats(1,11,13,13)));
        mainBlobs.add(new Blob("Dummy.java", new GitObjectStats(1,17,19,13)));
        Tree mainTree = new Tree(
                "main", new GitObjectStats(1, 28, 32, 0),
                new ArrayList<>(), mainBlobs
        );
        List<Blob> testBlobs = new ArrayList<>();
        testBlobs.add(new Blob("AppTest.java", new GitObjectStats(1,23,29,38)));
        Tree testTree = new Tree(
                "test", new GitObjectStats(1, 23, 29, 0),
                new ArrayList<>(), testBlobs
        );
        List<Tree> srcSubtrees = new ArrayList<>();
        srcSubtrees.add(mainTree);
        srcSubtrees.add(testTree);
        List<Tree> topSubtrees = new ArrayList<>();
        topSubtrees.add(new Tree(
                "src", new GitObjectStats(1, 51, 61, 0),
                srcSubtrees, new ArrayList<>()
        ));
        List<Blob> topBlobs = new ArrayList<>();
        topBlobs.add(new Blob("pom.xml",
                new GitObjectStats(1,3,7,18)));
        return new Tree(
                ".", new GitObjectStats(1,54,68,0),
                topSubtrees, topBlobs
        );
    }

    private static List<Blob> diffTree() {
        List<Blob> tree1 = new ArrayList<>();
        tree1.add(new Blob("pom.xml",
                new GitObjectStats(1,3,7,18)));
        tree1.add(new Blob("src/main/App.java",
                new GitObjectStats(1,11,13,13)));
        tree1.add(new Blob("src/main/Dummy.java",
                new GitObjectStats(1,17,19,13)));
        tree1.add(new Blob("src/test/AppTest.java",
                new GitObjectStats(1,23, 29,38)));
        return tree1;
    }

    private static List<Blob> initialTree() {
        List<Blob> tree1 = new ArrayList<>();
        tree1.add(new Blob("pom.xml",
                new GitObjectStats(0,0,0,18)));
        tree1.add(new Blob("src/main/java/foobar/App.java",
                new GitObjectStats(0,0,0,13)));
        tree1.add(new Blob("src/test/java/foobar/AppTest.java",
                new GitObjectStats(0,0,0,38)));
        return tree1;
    }

    private static Tree expectedInitialTree() {
        Blob appTestBlob = new Blob("AppTest.java", new GitObjectStats(0,0,0,38));
        List<Blob> testFoobarBlobs = new ArrayList<>();
        testFoobarBlobs.add(appTestBlob);
        Tree testFoobarTree = new Tree(
                "foobar", new GitObjectStats(0,0,0,38),
                new ArrayList<>(), testFoobarBlobs
        );
        List<Tree> testJavaFoobarSubtrees = new ArrayList<>();
        testJavaFoobarSubtrees.add(testFoobarTree);
        Tree testJavaTree = new Tree(
                "java", new GitObjectStats(0,0,0,38),
                testJavaFoobarSubtrees, new ArrayList<>()
        );
        List<Tree> testSubtrees = new ArrayList<>();
        testSubtrees.add(testJavaTree);
        Tree testTree = new Tree(
                "test", new GitObjectStats(0,0,0,38),
                testSubtrees, new ArrayList<>()
        );
        Blob appBlob = new Blob("App.java", new GitObjectStats(0,0,0,13));
        List<Blob> javaFoobarBlobs = new ArrayList<>();
        javaFoobarBlobs.add(appBlob);
        Tree javaFoobarTree = new Tree(
                "foobar", new GitObjectStats(0,0,0,13),
                new ArrayList<>(), javaFoobarBlobs
        );
        List<Tree> javaSubtrees = new ArrayList<>();
        javaSubtrees.add(javaFoobarTree);
        Tree javaTree = new Tree(
                "java", new GitObjectStats(0,0,0,13),
                javaSubtrees, new ArrayList<>()
        );
        List<Tree> mainSubtrees = new ArrayList<>();
        mainSubtrees.add(javaTree);
        Tree mainTree = new Tree(
                "main", new GitObjectStats(0,0,0,13),
                mainSubtrees, new ArrayList<>()
        );
        List<Tree> srcSubtrees = new ArrayList<>();
        srcSubtrees.add(mainTree);
        srcSubtrees.add(testTree);
        Tree srcTree = new Tree(
                "src", new GitObjectStats(0,0,0,51),
                srcSubtrees, new ArrayList<>()
        );
        List<Tree> topSubtrees = new ArrayList<>();
        topSubtrees.add(srcTree);
        List<Blob> topBlobs = new ArrayList<>();
        topBlobs.add(new Blob("pom.xml",
                new GitObjectStats(0,0,0,18)));
        return new Tree(
                ".", new GitObjectStats(0,0,0,69),
                topSubtrees, topBlobs
        );
    }
}
