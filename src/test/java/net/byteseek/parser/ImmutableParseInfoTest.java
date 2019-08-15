package net.byteseek.parser;

import org.junit.Test;

import static org.junit.Assert.*;

public class ImmutableParseInfoTest {

    @Test(expected=IllegalArgumentException.class)
    public void testConstructNullString() {
        new ImmutableParseInfo(null, 0);
    }

    @Test
    public void getString() throws Exception {
        testGetString("");
        testGetString(" ");
        testGetString("abcdefghijklmnopqrtsuvwxyz");
    }

    private void testGetString(String string) {
        ImmutableParseInfo info = new ImmutableParseInfo(string, 0);
        assertEquals(string, info.getString());
    }

    @Test
    public void getPosition() throws Exception {
        for (int i = 0; i < 100; i++) {
            ImmutableParseInfo info = new ImmutableParseInfo("test", i);
            assertEquals(i, info.getPosition());
        }
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        for (int i = 0; i < 100; i++) {
            testEqualsHashCode("", i);
            testEqualsHashCode("123", i);
            testEqualsHashCode("234", i);
        }
    }

    private void testEqualsHashCode(String string, int position) {
        ImmutableParseInfo info = new ImmutableParseInfo(string, position);
        assertFalse(info.equals(null));

        Object other = new Object();
        assertFalse(info.equals(other));

        ImmutableParseInfo same = new ImmutableParseInfo(string, position);
        ImmutableParseInfo different = new ImmutableParseInfo(string, position + 1);

        assertEquals(info, same);
        assertEquals(same, info);
        assertEquals(info.hashCode(), same.hashCode());

        assertNotEquals(info, different);
    }

    @Test
    public void testToString() {
        for (int i = 0; i < 1000; i += (i + 1)) {
            testToString("test", i);
            testToString("", i);
            testToString(" ", i);
        }
    }

    private void testToString(String string, int position) {
        ImmutableParseInfo info = new ImmutableParseInfo(string, position);
        assertTrue(info.toString().contains(ImmutableParseInfo.class.getSimpleName()));
        assertTrue(info.toString().contains("string"));
        assertTrue(info.toString().contains("position"));
    }

    @Test
    public void testCopyConstructor() {
        for (int i = 0; i < 1000; i += (i + 1)) {
            testCopyConstructor("test", i);
            testCopyConstructor("", i);
            testCopyConstructor(" ", i);
        }
    }

    private void testCopyConstructor(String string, int position) {
        ImmutableParseInfo info = new ImmutableParseInfo(string, position);
        ImmutableParseInfo copy = new ImmutableParseInfo(info);
        assertEquals(info.getPosition(), copy.getPosition());
        assertEquals(info.getString(), copy.getString());
        assertEquals(info, copy);
        assertEquals(info.hashCode(), copy.hashCode());
    }

}