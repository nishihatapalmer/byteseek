package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class SignedHash4SearcherTest {


    // Test constructors

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullSequence() {
        new SignedHash4Searcher((SequenceMatcher) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullString() {
        new SignedHash4Searcher((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyString() {
        new SignedHash4Searcher("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCharset() {
        new SignedHash4Searcher("ABCDEFG", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullByteArray() {
        new SignedHash4Searcher((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyByteArray() {
        new SignedHash4Searcher(new byte[0]);
    }

    // Test length.

    @Test
    public void testGetSequenceLength() throws Exception {
        AbstractSequenceSearcher s = new SignedHash4Searcher("A");
        assertEquals("Length correct", 1, s.getSequenceLength());

        s = new SignedHash4Searcher("AA");
        assertEquals("Length correct", 2, s.getSequenceLength());

        s = new SignedHash4Searcher("1234567890");
        assertEquals("Length correct", 10, s.getSequenceLength());
    }
}