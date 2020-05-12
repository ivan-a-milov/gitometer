package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GitObjectStatsTest extends TestCase
{
    public GitObjectStatsTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( GitObjectStatsTest.class );
    }

    public void testConstructorAndGetters() {
        GitObjectStats stats = new GitObjectStats(123, 345, 567, 978);
        assertEquals("Cnstr commits", stats.commits(), 123);
        assertEquals("Cnstr lines added", stats.linesAdded(), 345);
        assertEquals("Cnstr lines deleted", stats.linesDeleted(), 567);
        assertEquals("Lines modified", stats.linesModified(), 912);
        assertEquals("Cnstr total lines", stats.totalLines(), 978);
        assertEquals("Modification rate", 0.932f, stats.modificationRate(), 0.01);
    }

    public void testZeroTotalLineModificationRate() {
        GitObjectStats stats = new GitObjectStats(533, 58, 980, 0);
        assertEquals(
                "Modification rate should be 0.0f if total lines is 0",
                0.0f, stats.modificationRate(), 0.01
        );
    }

    public void testEquals() {
        GitObjectStats stats1 = new GitObjectStats(533, 58, 980, 25);
        GitObjectStats stats2 = new GitObjectStats(533, 58, 980, 25);
        assertEquals(stats1, stats1);
        assertEquals(stats1, stats2);
    }

    public void testEqualsNull() {
        GitObjectStats stats = new GitObjectStats(35, 86, 90, 22);
        assertFalse(stats.equals(null));
    }

    public void testEqualsNotInstanceOf() {
        GitObjectStats stats = new GitObjectStats(35, 86, 90, 22);
        assertFalse(stats.equals("foobar"));
    }

    public void testNotEquals() {
        GitObjectStats stats1 = new GitObjectStats(35, 86, 90, 22);
        GitObjectStats stats2 = new GitObjectStats(35, 86, 90, 1);
        GitObjectStats stats3 = new GitObjectStats(35, 86, 1, 22);
        GitObjectStats stats4 = new GitObjectStats(35, 1, 90, 22);
        GitObjectStats stats5 = new GitObjectStats(1, 1, 90, 22);
        assertFalse(stats1.equals(stats2));
        assertFalse(stats1.equals(stats3));
        assertFalse(stats1.equals(stats4));
        assertFalse(stats1.equals(stats5));
    }

    public void testToString() {
        GitObjectStats stats = new GitObjectStats(35, 86, 90, 22);
        String expected = "GitObjectStats(commits=35, linesAdded=86, linesDeleted=90, totalLines=22)";
        assertEquals(expected, stats.toString());
    }
}
