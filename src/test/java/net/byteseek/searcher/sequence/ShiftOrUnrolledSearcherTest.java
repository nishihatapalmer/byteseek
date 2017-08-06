package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShiftOrUnrolledSearcherTest {

    // Test constructors

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullSequence() {
        new ShiftOrUnrolledSearcher((SequenceMatcher) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullString() {
        new ShiftOrUnrolledSearcher((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyString() {
        new ShiftOrUnrolledSearcher("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCharset() {
        new ShiftOrUnrolledSearcher("ABCDEFG", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullByteArray() {
        new ShiftOrUnrolledSearcher((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyByteArray() {
        new ShiftOrUnrolledSearcher(new byte[0]);
    }

    // Test length.

    @Test
    public void testGetSequenceLength() throws Exception {
        AbstractSequenceSearcher s = new ShiftOrUnrolledSearcher("A");
        assertEquals("Length correct", 1, s.getSequenceLength() );

        s = new ShiftOrUnrolledSearcher("AA");
        assertEquals("Length correct", 2, s.getSequenceLength() );

        s = new ShiftOrUnrolledSearcher("1234567890");
        assertEquals("Length correct", 10, s.getSequenceLength() );
    }
}