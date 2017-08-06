package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class SignedHash3SearcherTest {


    // Test constructors

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullSequence() {
        new SignedHash3Searcher((SequenceMatcher) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullString() {
        new SignedHash3Searcher((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyString() {
        new SignedHash3Searcher("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCharset() {
        new SignedHash3Searcher("ABCDEFG", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullByteArray() {
        new SignedHash3Searcher((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyByteArray() {
        new SignedHash3Searcher(new byte[0]);
    }

    // Test length.

    @Test
    public void testGetSequenceLength() throws Exception {
        AbstractSequenceSearcher s = new SignedHash3Searcher("A");
        assertEquals("Length correct", 1, s.getSequenceLength());

        s = new SignedHash3Searcher("AA");
        assertEquals("Length correct", 2, s.getSequenceLength());

        s = new SignedHash3Searcher("1234567890");
        assertEquals("Length correct", 10, s.getSequenceLength());
    }
}