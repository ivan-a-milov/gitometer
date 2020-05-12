package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BlobTest extends TestCase
{
    public BlobTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( BlobTest.class );
    }

    public void testConstructorAndGetters() {
        GitObjectStats stats = new GitObjectStats(123, 345, 567, 978);
        Blob blob = new Blob("blob42-deeL4", stats, true);

        assertEquals("Cnstr name", "blob42-deeL4", blob.name());
        assertEquals("Cnstr stats",
                new GitObjectStats(123, 345, 567, 978),
                blob.stats()
        );
        assertTrue("Cnstr isBinary", blob.isBinary());
    }

    public void testNotBinaryByDefault() {
        Blob blob = stubBlob();
        assertFalse("Blob is not binary by default", blob.isBinary());
    }

    public void testEqualsNull() {
        Blob blob = stubBlob();
        assertFalse(blob.equals(null));
    }

    public void testEqualsThis() {
        Blob blob = stubBlob();
        assertEquals(blob, blob);
    }

    public void testEqualsNotInstanceOf() {
        Blob blob = stubBlob();
        assertFalse(blob.equals(new Object()));
    }

    public void testEquals() {
        GitObjectStats stats1 = new GitObjectStats(44, 33, 22, 11);
        Blob blob1 = new Blob("mohyae4J", stats1);

        GitObjectStats stats2 = new GitObjectStats(44, 33, 22, 11);
        Blob blob2 = new Blob("mohyae4J", stats2);

        assertEquals(blob1, blob2);
    }

    public void testNotEquals() {
        GitObjectStats stats1 = new GitObjectStats(44, 33, 22, 11);
        Blob blob1 = new Blob("ieNgi2xoo", stats1);

        Blob blob2 = new Blob("--ieNgi2xoo--", stats1);
        assertFalse("Not equals by name", blob1.equals(blob2));

        Blob blob3 = new Blob("ieNgi2xoo", stats1, true);
        assertFalse("Not equals by type (text/binary)", blob1.equals(blob3));

        GitObjectStats stats4 = new GitObjectStats(44, 33, 22, 9999);
        Blob blob4 = new Blob("ieNgi2xoo", stats4);
        assertFalse("Not equals by stats", blob1.equals(blob4));
    }

    private static Blob stubBlob() {
        GitObjectStats stats = new GitObjectStats(123, 345, 567, 978);
        Blob blob = new Blob("blob42-deeL4", stats);
        return blob;
    }
}
