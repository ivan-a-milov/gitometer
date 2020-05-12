package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

public class TreeTest extends TestCase
{
    public TreeTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( TreeTest.class );
    }

    public void testConstructorAndGetters() {
        GitObjectStats stats = new GitObjectStats(72, 986, 85, 99);
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new Blob("ahv6AeLai", new GitObjectStats(1, 2, 3,4)));
        blobs.add(new Blob("Iegh1aiGi", new GitObjectStats(5, 7, 0,8)));

        List<Tree> subtrees = new ArrayList<>();
        subtrees.add(new Tree("ephoh8uuT", stats, new ArrayList<>(), new ArrayList<>()));

        Tree tree = new Tree("tree-eech0Tu", stats, subtrees, blobs);
        assertEquals("Cnstr name", "tree-eech0Tu", tree.name());
        assertEquals("Cnstr stats",
                new GitObjectStats(72, 986, 85, 99),
                tree.stats()
        );

        List<Blob> blobs1 = new ArrayList<>();
        blobs1.add(new Blob("ahv6AeLai", new GitObjectStats(1, 2, 3,4)));
        blobs1.add(new Blob("Iegh1aiGi", new GitObjectStats(5, 7, 0,8)));
        assertEquals("Cnstr blobs", blobs1, tree.blobs());

        List<Tree> subtrees1 = new ArrayList<>();
        subtrees1.add(new Tree("ephoh8uuT", stats, new ArrayList<>(), new ArrayList<>()));
        assertEquals("Cnstr subtrees", subtrees1, tree.subtrees());
    }

    public void testEqualsNull() {
        Tree tree = minimalStubTree();
        assertFalse(tree.equals(null));
    }

    public void testEqualsThis() {
        Tree tree = minimalStubTree();
        assertEquals(tree, tree);
    }

    public void testEqualsNotInstanceOf() {
        Tree tree = minimalStubTree();
        assertFalse(tree.equals(new GitObjectStats(4,9,2,1)));
    }

    public void testEquals() {
        Tree tree1 = stubTree();
        Tree tree2 = stubTree();
        assertEquals(tree1, tree2);
    }

    public void testNotEqualsByName() {
        GitObjectStats stats = new GitObjectStats(1, 2, 3,4);
        Tree tree1 = new Tree("ajah9Vie8", stats, new ArrayList<>(), new ArrayList<>());
        Tree tree2 = new Tree("Eeb9poozo", stats, new ArrayList<>(), new ArrayList<>());
        assertFalse(tree1.equals(tree2));
    }

    public void testNotEqualsByBlobs() {
        GitObjectStats stats = new GitObjectStats(1, 2, 3,4);
        List<Blob> blobs1 = new ArrayList<>();
        blobs1.add(new Blob("pom.xml", new GitObjectStats(19, 28, 37,42)));
        List<Blob> blobs2 = new ArrayList<>();
        Tree tree1 = new Tree("foobar", stats, new ArrayList<>(), blobs1);
        Tree tree2 = new Tree("foobar", stats, new ArrayList<>(), blobs2);
        assertFalse(tree1.equals(tree2));
    }

    public void testNotEqualsBySubtrees() {
        GitObjectStats stats = new GitObjectStats(1, 2, 3,4);
        List<Tree> subtrees1 = new ArrayList<>();
        subtrees1.add(new Tree("foo", stats, new ArrayList<>(), new ArrayList<>()));
        List<Tree> subtrees2 = new ArrayList<>();
        subtrees2.add(new Tree("bar", stats, new ArrayList<>(), new ArrayList<>()));
        Tree tree1 = new Tree("foobar", stats, subtrees1, new ArrayList<>());
        Tree tree2 = new Tree("foobar", stats, subtrees2, new ArrayList<>());
        assertFalse(tree1.equals(tree2));
    }

    private static Tree stubTree() {
        GitObjectStats stats = new GitObjectStats(72, 986, 85, 99);
        List<Blob> blobs = new ArrayList<>();
        blobs.add(new Blob("ahv6AeLai", new GitObjectStats(1, 2, 3,4)));
        blobs.add(new Blob("Iegh1aiGi", new GitObjectStats(5, 7, 0,8)));
        List<Tree> subtrees = new ArrayList<>();
        subtrees.add(new Tree("ephoh8uuT", stats, new ArrayList<>(), new ArrayList<>()));
        Tree tree = new Tree("tree-eech0Tu", stats, subtrees, blobs);
        return tree;
    }

    private static Tree minimalStubTree() {
        return new Tree(
                "ephoh8uuT",
                new GitObjectStats(1, 1, 0,0),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
