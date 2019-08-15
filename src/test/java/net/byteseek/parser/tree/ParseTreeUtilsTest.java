/*
 * Copyright Matt Palmer 2012-2019, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.byteseek.parser.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.byteseek.utils.ByteUtils;
import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.parser.tree.node.StringNode;

import org.junit.Test;

public class ParseTreeUtilsTest {
    
    @Test
    public final void testGetFirstChild() throws ParseException {
        ParseTree parent = new ChildrenNode(ParseTreeType.SET,
                new IntNode(1), new StringNode("A string", StandardCharsets.ISO_8859_1));

        ParseTree firstChild = ParseTreeUtils.getFirstChild(parent);
        assertEquals("First child is integer", ParseTreeType.INTEGER, firstChild.getParseTreeType());
        assertEquals("First child value is 1", 1, firstChild.getIntValue());

        parent = new ChildrenNode(ParseTreeType.SET, new StringNode("A string", StandardCharsets.ISO_8859_1), new IntNode(1));

        firstChild = ParseTreeUtils.getFirstChild(parent);
        assertEquals("First child is String", ParseTreeType.STRING, firstChild.getParseTreeType());
        assertEquals("First child value is 'A string'", "A string", firstChild.getTextValue());

        try {
            firstChild = ParseTreeUtils.getFirstChild(null);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {
        }

        try {
            parent = new ChildrenNode(ParseTreeType.SET);
            firstChild = ParseTreeUtils.getFirstChild(parent);
            fail("Expected a parse exception if there are no children");
        } catch (ParseException expected) {
        }

    }


    @Test
    public final void testGetLastChild() throws ParseException {
        ParseTree parent = new ChildrenNode(ParseTreeType.SET, new StringNode("A string", StandardCharsets.ISO_8859_1), new IntNode(1));

        ParseTree lastChild = ParseTreeUtils.getLastChild(parent);
        assertEquals("Last child is integer", ParseTreeType.INTEGER, lastChild.getParseTreeType());
        assertEquals("Last child value is 1", 1, lastChild.getIntValue());

        parent = new ChildrenNode(ParseTreeType.SET, new IntNode(1), new StringNode("A string", StandardCharsets.ISO_8859_1));

        lastChild = ParseTreeUtils.getLastChild(parent);
        assertEquals("Last child is String", ParseTreeType.STRING, lastChild.getParseTreeType());
        assertEquals("Last child value is 'A string'", "A string", lastChild.getTextValue());

        try {
            lastChild = ParseTreeUtils.getLastChild(null);
            fail("Expected an illegal argument exception");
        } catch (IllegalArgumentException expected) {
        }

        try {
            parent = new ChildrenNode(ParseTreeType.SET);
            lastChild = ParseTreeUtils.getLastChild(parent);
            fail("Expected a parse exception if there are no children");
        } catch (ParseException expected) {
        }
    }


    @Test
    public void testGetChildIndexOfType() {
        try {
            ParseTreeUtils.getChildIndexOfType(null, 0, ParseTreeType.SET);
            fail("Expected IllegalArgumentException for null parse tree");
        } catch (IllegalArgumentException expected) {
        }

        ParseTree parent = new ChildrenNode(ParseTreeType.SET,
                new StringNode("A string", StandardCharsets.ISO_8859_1),
                new IntNode(1));
        testGetChildIndexOfType(parent, ParseTreeType.STRING, 0, 0);
        testGetChildIndexOfType(parent, ParseTreeType.INTEGER, 1, 0);
        testNoIndexForType(parent, ParseTreeType.BYTE);

        parent = new ChildrenNode(ParseTreeType.SET,
                new StringNode("A string", StandardCharsets.ISO_8859_1),
                new StringNode("Second string", StandardCharsets.ISO_8859_1));
        testGetChildIndexOfType(parent, ParseTreeType.STRING, 0, 0);
        testGetChildIndexOfType(parent, ParseTreeType.STRING, 1, 1);
        testNoIndexForType(parent, ParseTreeType.SET);
    }

    private void testNoIndexForType(ParseTree parent, ParseTreeType type) {
        assertEquals(parent.toString() + " looking for " + type, -1, ParseTreeUtils.getChildIndexOfType(parent, 0, type));
    }

    private void testGetChildIndexOfType(ParseTree parent, ParseTreeType childType, int index, int startIndex) {
        int childIndex = ParseTreeUtils.getChildIndexOfType(parent, startIndex, childType);
        assertEquals("Child index of node " + parent + " of type " + childType, index, childIndex);
    }


    @Test
    public final void testGetFirstRangeValue() throws ParseException {
        ParseTree parent = new ChildrenNode(ParseTreeType.RANGE, ByteNode.valueOf((byte) 1), ByteNode.valueOf((byte) 1));
        assertEquals("Value is 1", 1, ParseTreeUtils.getFirstRangeValue(parent));

        parent = new ChildrenNode(ParseTreeType.RANGE, ByteNode.valueOf((byte) 242), ByteNode.valueOf((byte) 10));
        assertEquals("Value is 242", 242, ParseTreeUtils.getFirstRangeValue(parent));

        for (ParseTreeType type : ParseTreeType.values()) {
            if (type != ParseTreeType.RANGE) {
                try {
                    ParseTreeUtils.getFirstRangeValue(new ChildrenNode(type));
                    fail("Expected a ParseException if the range node type is not RANGE.  Type is " + type);
                } catch (ParseException expected) {
                }
            }

            if (type != ParseTreeType.BYTE) {
                parent = new ChildrenNode(ParseTreeType.RANGE, new BaseNode(type), ByteNode.valueOf((byte) 96));
                try {
                    ParseTreeUtils.getFirstRangeValue(parent);
                    fail("Expected a ParseException if the child type is not a BYTE.  Type is " + type);
                } catch (ParseException excepted) {
                }
            }
        }


        try {
            parent = new ChildrenNode(ParseTreeType.RANGE, ByteNode.valueOf((byte) 54));
            ParseTreeUtils.getFirstRangeValue(parent);
            fail("Expected a ParseException with only one child");
        } catch (ParseException expected) {
        }

        try {
            parent = new ChildrenNode(ParseTreeType.RANGE,
                    ByteNode.valueOf((byte) 0),
                    ByteNode.valueOf((byte) 64),
                    ByteNode.valueOf((byte) 92));
            ParseTreeUtils.getFirstRangeValue(parent);
            fail("Expected a ParseException with three children");
        } catch (ParseException expected) {
        }

    }


    @Test
    public final void testGetSecondRangeValue() throws ParseException {
        ParseTree parent = new ChildrenNode(ParseTreeType.RANGE,
                ByteNode.valueOf((byte) 1),
                ByteNode.valueOf((byte) 1));
        assertEquals("Value is 1", 1, ParseTreeUtils.getSecondRangeValue(parent));

        parent = new ChildrenNode(ParseTreeType.RANGE,
                ByteNode.valueOf((byte) 242),
                ByteNode.valueOf((byte) 10));
        assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRangeValue(parent));

        for (ParseTreeType type : ParseTreeType.values()) {
            if (type != ParseTreeType.RANGE) {
                try {
                    ParseTreeUtils.getSecondRangeValue(new ChildrenNode(type));
                    fail("Expected a ParseException if the type was not Range.  Type is " + type);
                } catch (ParseException expected) {
                }
            }

            if (type != ParseTreeType.BYTE) {
                parent = new ChildrenNode(ParseTreeType.RANGE,
                        ByteNode.valueOf((byte) 96),
                        new BaseNode(type));
                try {
                    ParseTreeUtils.getSecondRangeValue(parent);
                    fail("Expected a ParseException if the second child type has is not a BYTE.  Type is " + type);
                } catch (ParseException excepted) {
                }
            }
        }


        try {
            parent = new ChildrenNode(ParseTreeType.RANGE, ByteNode.valueOf((byte) 255));
            ParseTreeUtils.getSecondRangeValue(parent);
            fail("Expected a ParseException with only one child");
        } catch (ParseException expected) {
        }

        try {
            parent = new ChildrenNode(ParseTreeType.RANGE,
                    ByteNode.valueOf((byte) 0),
                    ByteNode.valueOf((byte) 64),
                    ByteNode.valueOf((byte) 92));
            ParseTreeUtils.getSecondRangeValue(parent);
            fail("Expected a ParseException with three children");
        } catch (ParseException expected) {
        }
    }

    @Test
    public final void testGetFirstRepeatValue() throws ParseException {
        ParseTree parent = new ChildrenNode(ParseTreeType.REPEAT, new IntNode(1));
        assertEquals("Value is 1", 1, ParseTreeUtils.getFirstRepeatValue(parent));

        parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX,
                new IntNode(2420), new IntNode(10));
        assertEquals("Value is 2420", 2420, ParseTreeUtils.getFirstRepeatValue(parent));

        for (ParseTreeType type : ParseTreeType.values()) {
            if (type != ParseTreeType.REPEAT &&
                    type != ParseTreeType.REPEAT_MIN_TO_MANY &&
                    type != ParseTreeType.REPEAT_MIN_TO_MAX) {
                try {
                    ParseTreeUtils.getFirstRepeatValue(new ChildrenNode(type));
                    fail("Expected a ParseException if the repeat node type is not a repeating node.  Type is " + type);
                } catch (ParseException expected) {
                }
            }

            if (type != ParseTreeType.INTEGER) {
                parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MANY, new BaseNode(type), new IntNode(96));
                try {
                    ParseTreeUtils.getFirstRepeatValue(parent);
                    fail("Expected a ParseException if the child type has no integer value.  Type is " + type);
                } catch (ParseException excepted) {
                }
            }
        }

        try {
            parent = new ChildrenNode(ParseTreeType.REPEAT, new IntNode(-1), new IntNode(30));
            ParseTreeUtils.getFirstRepeatValue(parent);
            fail("Expected a ParseException if a value is less than 0");
        } catch (ParseException expected) {
        }
    }

    @Test
    public final void testGetSecondRepeatValue() throws ParseException {
        ParseTree parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX,
                new IntNode(1), new IntNode(10));
        assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRepeatValue(parent));

        parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX,
                new IntNode(2420), new IntNode(10));
        assertEquals("Value is 10", 10, ParseTreeUtils.getSecondRepeatValue(parent));

        for (ParseTreeType type : ParseTreeType.values()) {
            if (type != ParseTreeType.REPEAT &&
                    type != ParseTreeType.REPEAT_MIN_TO_MANY &&
                    type != ParseTreeType.REPEAT_MIN_TO_MAX) {
                try { // this is buggy anyway...
                    ParseTreeUtils.getSecondRepeatValue(new ChildrenNode(type));
                    fail("Expected a ParseException if the repeat node type is not a repeating node.  Type is " + type);
                } catch (ParseException expected) {
                }
            }

            if (type != ParseTreeType.INTEGER) {
                parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX,
                        new IntNode(96), new BaseNode(type));
                try {
                    ParseTreeUtils.getSecondRepeatValue(parent);
                    fail("Expected a ParseException if the child type has no integer value.  Type is " + type);
                } catch (ParseException excepted) {
                }
            }
        }

        try {
            parent = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX,
                    new IntNode(30), new IntNode(-1));
            ParseTreeUtils.getSecondRepeatValue(parent);
            fail("Expected a ParseException if a value is less than 0");
        } catch (ParseException expected) {
        }
    }

    @Test
    public final void testAddByteValues() throws ParseException {
        for (int i = 0; i < 256; i++) {
            testAddByteValues((byte) i);
        }
    }

    private void testAddByteValues(byte value) throws ParseException {
        ParseTree byteNode = new ByteNode(value);
        Set<Byte> set = new HashSet<Byte>();
        ParseTreeUtils.addByteValues(byteNode, set);
        assertEquals("Single byte value only has one value", 1, set.size());
        assertTrue("Single byte value has correct value for byte " + value, set.contains(value));

        byteNode = new ByteNode(value, false);
        set = new HashSet<Byte>();
        ParseTreeUtils.addByteValues(byteNode, set);
        assertEquals("Single byte value not inverted only has one value", 1, set.size());
        assertTrue("Single byte value not inverted has correct value for byte " + value, set.contains(value));

        byteNode = new ByteNode(value, true);
        set = new HashSet<Byte>();
        ParseTreeUtils.addByteValues(byteNode, set);
        assertEquals("Single byte value inverted has 255 values", 255, set.size());
        assertFalse("Single byte value inverted does not have original value " + value, set.contains(value));
    }

    @Test
    public void testAddWildBitBytesToSet() throws Exception {
        final Set<Byte> set = new HashSet<Byte>();
        for (int mask = 0; mask < 256; mask++) {
            final byte maskByte = (byte) mask;
            final ParseTree maskNode = ByteNode.valueOf(maskByte);
            for (int value = 0; value < 256; value++) {
                final byte valueByte = (byte) value;
                set.clear();
                final ParseTree valueNode = ByteNode.valueOf(valueByte);
                final ParseTree wildbit = new ChildrenNode(ParseTreeType.WILDBIT, maskNode, valueNode);
                ParseTreeUtils.addWildBitBytes(wildbit, set);
                checkWildBitSet(set, valueByte, maskByte);

                final ParseTree setNode = new ChildrenNode(ParseTreeType.SET, wildbit);
                Set<Byte> sameBytes = ParseTreeUtils.getSetValues(setNode);
                assertEquals(sameBytes.size(), set.size());
                for (Byte b : set) {
                    assertTrue(sameBytes.contains(b));
                }
            }
        }
    }

    @Test
    public void testAddWildBitAnyBytesToSet() throws Exception {
        final Set<Byte> set = new HashSet<Byte>();
        for (int mask = 0; mask < 256; mask++) {
            final byte maskByte = (byte) mask;
            final ParseTree maskNode = ByteNode.valueOf(maskByte);
            for (int value = 0; value < 256; value++) {
                final byte valueByte = (byte) value;
                set.clear();
                final ParseTree valueNode = ByteNode.valueOf(valueByte);
                final ParseTree wildbit = new ChildrenNode(ParseTreeType.ANYBITS, maskNode, valueNode);
                ParseTreeUtils.addWildBitAnyBytes(wildbit, set);
                checkWildBitAnySet(set, valueByte, maskByte);

                final ParseTree setNode = new ChildrenNode(ParseTreeType.SET, wildbit);
                Set<Byte> sameBytes = ParseTreeUtils.getSetValues(setNode);
                assertEquals(sameBytes.size(), set.size());
                for (Byte b : set) {
                    assertTrue(sameBytes.contains(b));
                }
            }
        }
    }

    private void checkWildBitSet(Set<Byte> set, byte value, byte mask) {
        byte valueToMatch = (byte) (value & mask);
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            byte theByte = (byte) byteValue;
            if ((theByte & mask) == valueToMatch) {
                assertTrue(set.contains(theByte));
            } else {
                assertFalse(set.contains(theByte));
            }
        }
    }

    @Test(expected=ParseException.class)
    public void testAddNoneWildBitNodeException() throws Exception {
        final Set<Byte> set = new HashSet<Byte>();
        final ParseTree maskNode = ByteNode.valueOf((byte) 0xA3);
        final ParseTree valueNode = ByteNode.valueOf((byte) 0xFF);
        final ParseTree wildbit = new ChildrenNode(ParseTreeType.ANYBITS, maskNode, valueNode);
        ParseTreeUtils.addWildBitBytes(wildbit, set);
    }

    @Test(expected=ParseException.class)
    public void testAddNoneWildBitAnyNodeException() throws Exception {
        final Set<Byte> set = new HashSet<Byte>();
        final ParseTree maskNode = ByteNode.valueOf((byte) 0xA3);
        final ParseTree valueNode = ByteNode.valueOf((byte) 0xFF);
        final ParseTree wildbit = new ChildrenNode(ParseTreeType.WILDBIT, maskNode, valueNode);
        ParseTreeUtils.addWildBitAnyBytes(wildbit, set);
    }

    private void checkWildBitAnySet(Set<Byte> set, byte value, byte mask) {
        byte valueNotToMatch = (byte) (~(value) & mask);
        for (int byteValue = 0; byteValue < 256; byteValue++) {
            byte theByte = (byte) byteValue;
            if ((theByte & mask) != valueNotToMatch) {
                assertTrue(set.contains(theByte));
            } else {
                assertFalse(set.contains(theByte));
            }
        }
    }

    @Test
    public final void testGetRangeValues() throws ParseException {
        testRangeValues(0, 0);
        testRangeValues(0, 255);
        testRangeValues(255, 255);
        testRangeValues(1, 2);
        testRangeValues(254, 255);
        testRangeValues(32, 127);
        for (int i = 0; i < 256; i++) {
            testRangeValues(0, i);
            testRangeValues(i, 255 - i);
        }
    }

    private void testRangeValues(int value1, int value2) throws ParseException {
        ParseTree rangeNode = buildRangeNode(value1, value2, false);
        Set<Byte> bytes = new HashSet<Byte>(256);
        ParseTreeUtils.addRangeBytes(rangeNode, bytes);
        validateRangeValues(bytes, value1, value2, false);

        rangeNode = buildRangeNode(value2, value1, false);
        bytes = new HashSet<Byte>(256);
        ParseTreeUtils.addRangeBytes(rangeNode, bytes);
        validateRangeValues(bytes, value1, value2, false);

        rangeNode = buildRangeNode(value2, value1, true);
        bytes = new HashSet<Byte>(256);
        ParseTreeUtils.addRangeBytes(rangeNode, bytes);
        validateRangeValues(bytes, value1, value2, true);

        rangeNode = buildRangeNode(value1, value2, true);
        bytes = new HashSet<Byte>(256);
        ParseTreeUtils.addRangeBytes(rangeNode, bytes);
        validateRangeValues(bytes, value1, value2, true);
    }


    private void validateRangeValues(Set<Byte> values, int range1, int range2, boolean inverted) {
        if (inverted) {
            if (range1 > range2) {
                for (int value = 0; value < range2; value++) {
                    Byte b = Byte.valueOf((byte) value);
                    assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
                    values.remove(b);
                }
                for (int value = range1 + 1; value < 256; value++) {
                    Byte b = Byte.valueOf((byte) value);
                    assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
                    values.remove(b);
                }
                assertTrue("No more bytes in values set", values.isEmpty());
            } else {
                for (int value = 0; value < range1; value++) {
                    Byte b = Byte.valueOf((byte) value);
                    assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
                    values.remove(b);
                }
                for (int value = range2 + 1; value < 256; value++) {
                    Byte b = Byte.valueOf((byte) value);
                    assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
                    values.remove(b);
                }
                assertTrue("No more bytes in values set", values.isEmpty());
            }
        } else {
            if (range1 > range2) {
                for (int value = range2; value <= range1; value++) {
                    Byte b = Byte.valueOf((byte) value);
                    assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
                    values.remove(b);
                }
                assertTrue("No more bytes in values set", values.isEmpty());
            } else {
                for (int value = range1; value <= range2; value++) {
                    Byte b = Byte.valueOf((byte) value);
                    assertTrue("Byte is in range " + range1 + ":" + range2, values.contains(b));
                    values.remove(b);
                }
                assertTrue("No more bytes in values set", values.isEmpty());
            }
        }
    }

    @Test
    public final void testAddStringAsSet() throws ParseException {
        testAddStringAsSet("123", new byte[]{'1', '2', '3'});
        testAddStringAsSet("a", new byte[]{'a'});
        testAddStringAsSet("abcdefghijklmnopqrstuvwxyz", new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'});
    }

    private void testAddStringAsSet(String string, byte[] bytes) throws ParseException {
        ParseTree node = new StringNode(string, StandardCharsets.ISO_8859_1);
        Set<Byte> byteSet = new HashSet<Byte>();
        ParseTreeUtils.addStringBytes(node, byteSet);
        assertEquals("String " + string + " has correct bytes", ByteUtils.toSet(bytes), byteSet);
    }

    @Test
    public final void testAddCaseStringAsSet() throws ParseException {
        testAddCaseStringAsSet("123", new byte[]{'1', '2', '3'});
        testAddCaseStringAsSet("a", new byte[]{'a', 'A'});
        testAddCaseStringAsSet("abcdefghijklmnopqrstuvwxyz",
                new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'});
    }

    private void testAddCaseStringAsSet(String string, byte[] bytes) throws ParseException {
        ParseTree node = new StringNode(string, StandardCharsets.ISO_8859_1);
        Set<Byte> byteSet = new HashSet<Byte>();
        ParseTreeUtils.addCaseInsensitiveStringBytes(node, byteSet);
        assertEquals("String " + string + " has correct bytes", ByteUtils.toSet(bytes), byteSet);
    }

    @Test
    public final void testIllegalSetTypes() {
        for (ParseTreeType type : ParseTreeType.values()) {
            if (type != ParseTreeType.BYTE &&
                    type != ParseTreeType.SET &&
                    type != ParseTreeType.RANGE &&
//                    type != ParseTreeType.ALL_BITMASK &&
//                    type != ParseTreeType.ANY_BITMASK &&
                    type != ParseTreeType.ANYBITS &&
                    type != ParseTreeType.WILDBIT &&
                    type != ParseTreeType.STRING &&
                    type != ParseTreeType.CASE_INSENSITIVE_STRING &&
                    type != ParseTreeType.ANY) {
                ParseTree node = new ChildrenNode(ParseTreeType.SET, new BaseNode(type));
                try {
                    ParseTreeUtils.getSetValues(node);
                    fail("getSetValues: expected a parse exception for set child node of type " + type);
                } catch (ParseException expected) {
                }
                try {
                    ParseTreeUtils.calculateSetValues(node);
                    fail("calculateSetValues: expected a parse exception for set child node of type " + type);
                } catch (ParseException expected) {
                }
                try {
                    ParseTreeUtils.addSetValues(node, new HashSet<Byte>());
                    fail("addSetValues: expected a parse exception for set child node of type " + type);
                } catch (ParseException expected) {
                }
            }
        }
    }

    @Test
    public final void testByteSetValues() throws ParseException {
        testByteSet((byte) 0x00);
        testByteSet((byte) 0x00, (byte) 0xFF);
        testByteSet((byte) 0x00, (byte) 0xFF);
        testByteSet((byte) 0xFF, (byte) 0x00);
        testByteSet((byte) 0xFF, (byte) 0xde, (byte) 0xad, (byte) 0xbe, (byte) 0xef);
    }

    @Test
    public final void testRangeSetValues() throws ParseException {
        testRangeSet(0, 0);
        testRangeSet(1, 128);
        testRangeSet(1, 20);
        for (int i = 0; i < 256; i++) {
            testRangeSet(i, 255 - i);
        }
    }

    /*
    @Test
    public void testAllBitmaskSet() throws ParseException {
        for (int i = 0; i < 256; i++) {
            testAllBitmaskSet(i);
        }
    }

    @Test
    public void testAnyBitmaskSet() throws ParseException {
        testAnyBitmaskSet(128);
        for (int i = 0; i < 256; i++) {
            testAnyBitmaskSet(i);
        }
    }
*/

    @Test(expected=ParseException.class)
    public void testWildBitNotByteChildren() throws ParseException {
        ParseTree node = new ChildrenNode(ParseTreeType.WILDBIT, new IntNode(1), new IntNode(2));
        Set<Byte> bytes = new HashSet<Byte>();
        ParseTreeUtils.addWildBitBytes(node, bytes);
    }


    @Test(expected=ParseException.class)
    public void testWildBitOneChildren() throws ParseException {
        ParseTree node = new ChildrenNode(ParseTreeType.WILDBIT, new ByteNode((byte) 1));
        Set<Byte> bytes = new HashSet<Byte>();
        ParseTreeUtils.addWildBitBytes(node, bytes);
    }

    @Test(expected=ParseException.class)
    public void testWildBitThreeChildren() throws ParseException {
        ParseTree node = new ChildrenNode(ParseTreeType.WILDBIT, new ByteNode((byte) 1), new ByteNode((byte) 1), new ByteNode((byte) 1));
        Set<Byte> bytes = new HashSet<Byte>();
        ParseTreeUtils.addWildBitBytes(node, bytes);
    }

    @Test(expected=ParseException.class)
    public void testWildBitAnyNotByteChildren() throws ParseException {
        ParseTree node = new ChildrenNode(ParseTreeType.ANYBITS, new IntNode(1), new IntNode(2));
        Set<Byte> bytes = new HashSet<Byte>();
        ParseTreeUtils.addWildBitAnyBytes(node, bytes);
    }

    @Test(expected=ParseException.class)
    public void testWildBitAnyOneChildren() throws ParseException {
        ParseTree node = new ChildrenNode(ParseTreeType.ANYBITS, new ByteNode((byte) 1));
        Set<Byte> bytes = new HashSet<Byte>();
        ParseTreeUtils.addWildBitAnyBytes(node, bytes);
    }

    @Test(expected=ParseException.class)
    public void testWildBitAnyThreeChildren() throws ParseException {
        ParseTree node = new ChildrenNode(ParseTreeType.ANYBITS, new ByteNode((byte) 1), new ByteNode((byte) 1), new ByteNode((byte) 1));
        Set<Byte> bytes = new HashSet<Byte>();
        ParseTreeUtils.addWildBitAnyBytes(node, bytes);
    }

    @Test
    public final void testStringSet() throws ParseException {
        testStringSet("");
        testStringSet(" ");
        testStringSet("ABC123abc123");
        testStringSet("\t£%*\n");
    }

    @Test
    public final void testCaseInsensitiveStringSet() throws ParseException {
        testCaseInsensitiveStringSet("");
        testCaseInsensitiveStringSet(" ");
        testCaseInsensitiveStringSet("ABC123abc123");
        testCaseInsensitiveStringSet("\t£%*\n");
    }

    @Test
    public final void testNestedByteSets() throws ParseException {
        testNestedByteSets((byte) 0x00);
        testNestedByteSets((byte) 0x00, (byte) 0xff);
        testNestedByteSets((byte) 0x7f, (byte) 0x01, (byte) 0x3c);
        testNestedByteSets((byte) 0x01, (byte) 0x02, (byte) 0xfe, (byte) 0xde, (byte) 0x74);
    }

    @Test
    public final void testRangeAndByteSet() throws ParseException {
        testRangeAndByteSet(8, 12, (byte) 0x34);
        testRangeAndByteSet(0, 255, (byte) 0x80, (byte) 0x7f);
        testRangeAndByteSet(0, 0, (byte) 0xff);
    }

    private void testNestedByteSets(byte... values) throws ParseException {
        // non inverted set of non inverted set of bytes
        ParseTree byteSet = buildSet(false, buildByteSet(false, values));
        Set<Byte> expected = ByteUtils.toSet(values);
        testSetMethods("straight nested byte set", expected, byteSet);

        // Add some byte nodes directly to the parent set:
        byteSet = combineByteValues(byteSet, (byte) 0x3c, (byte) 0x83);
        ByteUtils.addAll(new byte[]{(byte) 0x3c, (byte) 0x83}, expected);
        testSetMethods("Straight nested byte set with additional byte nodes", expected, byteSet);

        // non-inverted set of inverted set of bytes
        byteSet = buildSet(false, buildByteSet(true, values));
        expected = ByteUtils.invertedSet(ByteUtils.toSet(values));
        testSetMethods("straight set of inverted set of bytes", expected, byteSet);

        // Add some byte nodes directly to the parent set:
        byteSet = combineByteValues(byteSet, (byte) 0x3c, (byte) 0x83);
        ByteUtils.addAll(new byte[]{(byte) 0x3c, (byte) 0x83}, expected);
        testSetMethods("Straight set of inverted set of bytes with additional byte nodes", expected, byteSet);

        // inverted set of non inverted set of bytes
        byteSet = buildSet(true, buildByteSet(false, values));
        expected = ByteUtils.invertedSet(ByteUtils.toSet(values));
        testInvertedSetMethods("inverted set of non inverted set of bytes", expected, byteSet);

        // Add some byte nodes directly to the parent set:
        byteSet = combineByteValues(byteSet, (byte) 0x3c, (byte) 0x83);
        expected = ByteUtils.toSet(values);
        ByteUtils.addAll(new byte[]{(byte) 0x3c, (byte) 0x83}, expected);
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("Inverted set of non-inverted set of bytes plus some additional byte nodes", expected, byteSet);

        // inverted set of inverted set of bytes
        byteSet = buildSet(true, buildByteSet(true, values));
        expected = ByteUtils.toSet(values);
        testInvertedSetMethods("inverted set of inverted set of bytes", expected, byteSet);

        // Add some byte nodes directly to the parent set:
        byteSet = combineByteValues(byteSet, (byte) 0x3c, (byte) 0x83);
        expected = ByteUtils.invertedSet(ByteUtils.toSet(values));
        ByteUtils.addAll(new byte[]{(byte) 0x3c, (byte) 0x83}, expected);
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("Inverted set of non-inverted set of bytes plus some additional byte nodes", expected, byteSet);
    }

    private void testRangeAndByteSet(int start, int end, byte... values) throws ParseException {
        // Straight set of range and bytes
        ParseTree set = buildSet(false, buildRangeNode(start, end, false));
        set = combineByteValues(set, values);
        Set<Byte> expected = new HashSet<Byte>();
        ByteUtils.addBytesInRange(start, end, expected);
        ByteUtils.addBytes(expected, values);
        testSetMethods("Mixed set of range and bytes", expected, set);

        // Inverted set of range and bytes
        set = buildSet(true, buildRangeNode(start, end, false));
        set = combineByteValues(set, values);
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("Inverted mixed set of range and  bytes", expected, set);

        // Straight set of inverted range and non-inverted bytes
        set = buildSet(false, buildRangeNode(start, end, true));
        set = combineByteValues(set, values);
        expected.clear();
        ByteUtils.addBytesNotInRange(start, end, expected);
        ByteUtils.addBytes(expected, values);
        testSetMethods("Mixed set of inverted range and bytes", expected, set);

        // Inverted set of inverted range and non-inverted bytes
        set = buildSet(true, buildRangeNode(start, end, true));
        set = combineByteValues(set, values);
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("Inverted set of inverted range and bytes", expected, set);
    }


    @Test
    public final void testAllSet() throws ParseException {
        // test straight any set:
        ParseTree set = buildSet(false, new BaseNode(ParseTreeType.ANY));
        Set<Byte> expected = new HashSet<Byte>(320);
        ByteUtils.addAllBytes(expected);
        testSetMethods("All set", expected, set);

        // test inverted any set:
        set = buildSet(true, new BaseNode(ParseTreeType.ANY));
        expected = new HashSet<Byte>(0);
        testInvertedSetMethods("All set", expected, set);
    }


    private void testByteSet(byte... values) throws ParseException {
        // Test a straight set of bytes
        ParseTree byteSet = buildByteSet(false, values);
        Set<Byte> expected = ByteUtils.toSet(values);
        testSetMethods("byte set" + ByteUtils.bytesToString(false, values), expected, byteSet);

        // Test an inverted set of bytes
        byteSet = buildByteSet(true, values);
        expected = ByteUtils.invertedSet(ByteUtils.toSet(values));
        testInvertedSetMethods("byte set" + ByteUtils.bytesToString(false, values), expected, byteSet);

        // Test a set of inverted bytes
        List<ParseTree> valueNodes = new ArrayList<ParseTree>();
        expected = new HashSet<Byte>(320);
        for (byte value : values) {
            valueNodes.add(ByteNode.valueOf(value, true));
            expected.addAll(ByteUtils.invertedSet(value));
        }
        byteSet = new ChildrenNode(ParseTreeType.SET, valueNodes);

        testSetMethods("byte set" + ByteUtils.bytesToString(false, values), expected, byteSet);

        // Test an inverted set of inverted bytes

        expected = new HashSet<Byte>(320);
        valueNodes = new ArrayList<ParseTree>();
        for (byte value : values) {
            valueNodes.add(ByteNode.valueOf(value, true));
            expected.addAll(ByteUtils.invertedSet(value));
        }
        byteSet = new ChildrenNode(ParseTreeType.SET, valueNodes, true);
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("byte set" + ByteUtils.bytesToString(false, values), expected, byteSet);
    }


    private void testRangeSet(int start, int end) throws ParseException {
        // Test a straight set of one range:
        ParseTree byteSet = buildSet(false, buildRangeNode(start, end, false));

        Set<Byte> expected = new HashSet<Byte>(256);
        ByteUtils.addBytesInRange(start, end, expected);
        testSetMethods("range: " + start + "-" + end, expected, byteSet);

        // Test an inverted set of one range:
        byteSet = buildSet(true, buildRangeNode(start, end, false));

        expected = new HashSet<Byte>(256);
        ByteUtils.addBytesNotInRange(start, end, expected);
        testInvertedSetMethods("range: " + start + "-" + end, expected, byteSet);

        // Test a set of an inverted range:
        byteSet = buildSet(false, buildRangeNode(start, end, true));

        expected = new HashSet<Byte>(256);
        ByteUtils.addBytesNotInRange(start, end, expected);
        testSetMethods("range: " + start + "-" + end, expected, byteSet);

        // Test an inverted set of an inverted range:
        byteSet = buildSet(true, buildRangeNode(start, end, true));

        expected = new HashSet<Byte>(256);
        ByteUtils.addBytesInRange(start, end, expected);
        testInvertedSetMethods("range: " + start + "-" + end, expected, byteSet);
    }

    private void testStringSet(final String string) throws ParseException {
        // Test straight set with a single string:
        ParseTree set = buildSet(false, new StringNode(string, StandardCharsets.ISO_8859_1));
        Set<Byte> expected = new HashSet<Byte>();
        ByteUtils.addStringBytes(string, expected);
        testSetMethods("String set: " + string, expected, set);

        // Test inverted set with a single string:
        set = buildSet(true, new StringNode(string, StandardCharsets.ISO_8859_1));
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("Inverted string set " + string, expected, set);
    }

    private void testCaseInsensitiveStringSet(final String string) throws ParseException {
        // Test straight set with a single string:
        ParseTree set = buildSet(false, new StringNode(string, StandardCharsets.ISO_8859_1, ParseTreeType.CASE_INSENSITIVE_STRING));
        Set<Byte> expected = new HashSet<Byte>();
        ByteUtils.addCaseInsensitiveStringBytes(string, expected);
        testSetMethods("Case insensitive string set: " + string, expected, set);

        // Test inverted set with a single string:
        set = buildSet(true, new StringNode(string, StandardCharsets.ISO_8859_1));
        expected = ByteUtils.invertedSet(expected);
        testInvertedSetMethods("Inverted case insensitive string set " + string, expected, set);
    }

    private void testSetMethods(String description, Set<Byte> expected, ParseTree byteSet) throws ParseException {
        Set<Byte> result = ParseTreeUtils.getSetValues(byteSet);
        assertEquals(description + " Get set of bytes are equal " + expected, expected, result);

        result = ParseTreeUtils.calculateSetValues(byteSet);
        assertEquals(description + " Calculate set of bytes are equal " + expected, expected, result);

        result.clear();
        ParseTreeUtils.addSetValues(byteSet, result);
        assertEquals(description + " Add set of bytes are equal " + expected, expected, result);
    }

    private void testInvertedSetMethods(String description, Set<Byte> expected, ParseTree byteSet) throws ParseException {
        Set<Byte> result = ParseTreeUtils.getSetValues(byteSet);
        assertFalse(description + " Inverted get set of bytes are not equal " + expected, expected.equals(result));

        result = ParseTreeUtils.calculateSetValues(byteSet);
        assertEquals(description + " Inverted calculate set of bytes are equal " + expected, expected, result);

        result.clear();
        ParseTreeUtils.addSetValues(byteSet, result);
        assertEquals(description + " Inverted add set of bytes are equal " + expected, expected, result);
    }

    private ParseTree buildSet(boolean inverted, final ParseTree... children) {
        return new ChildrenNode(ParseTreeType.SET, inverted, children);
    }

    private ParseTree buildRangeNode(int start, int end, boolean inverted) {
        return new ChildrenNode(ParseTreeType.RANGE, inverted,
                ByteNode.valueOf((byte) start), ByteNode.valueOf((byte) end));
    }

    private ParseTree buildByteSet(boolean invertedSet, byte... values) {
        List<ParseTree> byteNodes = new ArrayList<ParseTree>();
        for (byte value : values) {
            byteNodes.add(ByteNode.valueOf(value));
        }
        return new ChildrenNode(ParseTreeType.SET, byteNodes, invertedSet);
    }

    private ParseTree combineByteValues(ParseTree set, byte... values) {
        List<ParseTree> byteNodes = new ArrayList<ParseTree>();
        for (ParseTree value : set) {
            byteNodes.add(value);
        }
        for (byte value : values) {
            byteNodes.add(ByteNode.valueOf(value));
        }
        return new ChildrenNode(ParseTreeType.SET, byteNodes, set.isValueInverted());
    }

}
