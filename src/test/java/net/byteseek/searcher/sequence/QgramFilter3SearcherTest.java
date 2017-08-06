package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class QgramFilter3SearcherTest {

    // Test constructors

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullSequence() {
        new QgramFilter3Searcher((SequenceMatcher) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullString() {
        new QgramFilter3Searcher((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyString() {
        new QgramFilter3Searcher("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCharset() {
        new QgramFilter3Searcher("ABCDEFG", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullByteArray() {
        new QgramFilter3Searcher((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyByteArray() {
        new QgramFilter3Searcher(new byte[0]);
    }

    // Test length.

    @Test
    public void testGetSequenceLength() throws Exception {
        AbstractSequenceSearcher s = new QgramFilter3Searcher("A");
        assertEquals("Length correct", 1, s.getSequenceLength() );

        s = new QgramFilter3Searcher("AA");
        assertEquals("Length correct", 2, s.getSequenceLength() );

        s = new QgramFilter3Searcher("1234567890");
        assertEquals("Length correct", 10, s.getSequenceLength() );
    }

}