package net.byteseek.searcher.sequence;

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class HorspoolSearcherTest {

    // Test constructors

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullSequence() {
        new HorspoolSearcher((SequenceMatcher) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullString() {
        new HorspoolSearcher((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyString() {
        new HorspoolSearcher("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCharset() {
        new HorspoolSearcher("ABCDEFG", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullByteArray() {
        new HorspoolSearcher((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyByteArray() {
        new HorspoolSearcher(new byte[0]);
    }

    @Test
    public void testGetSequenceLength() throws Exception {
        AbstractSequenceSearcher s = new HorspoolSearcher("A");
        assertEquals("Length correct", 1, s.getSequenceLength() );

        s = new HorspoolSearcher("AA");
        assertEquals("Length correct", 2, s.getSequenceLength() );

        s = new HorspoolSearcher("1234567890");
        assertEquals("Length correct", 10, s.getSequenceLength() );
    }

    @Test
    public void testToString() throws Exception {

    }
}