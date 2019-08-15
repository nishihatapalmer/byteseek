/*
 * Copyright Matt Palmer 2012-13, All rights reserved.
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

package net.byteseek.parser.regex;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.byteseek.utils.ByteUtils;
import net.byteseek.parser.ParseException;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.ParseTreeUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Matt Palmer
 */
public class RegexParserTest {

    Random random;
    RegexParser parser;

    @Before
    public void setUp() {
        random = new Random();
        parser = new RegexParser();
    }

    @After
    public void tearDown() {
        parser = null;
    }


    @Test
    /**
     * Test null or empty input.
     *
     * A null or empty input should throw a ParseException.
     */
    public final void testNullOrEmpty() {
        expectParseException("Null input", null);
        expectParseException("Empty input", "");
    }


    @Test
    /**
     * Test whitespace.
     *
     * Whitespace consists of space, tab, new line and carriage return.
     *
     * Whitespace should be stripped out between any other elements
     * by the parser, leaving nothing to produce a ParseTree with.
     * In turn this should cause a ParseException to be thrown.
     *
     * Note: still unsure about throwing a ParseException when the
     * syntax of the expression is perfectly valid.  It's just that
     * it results in a null ParseTree if that is all there is.
     */
    public final void testWhiteSpace() {
        expectParseException("Tab", "\t");
        expectParseException("Tabs", "\t\t\t");
        expectParseException("Newline", "\n");
        expectParseException("Newlines", "\n\n");
        expectParseException("Carriage return", "\r");
        expectParseException("Carriage returns", "\r\r");
        expectParseException("Space", " ");
        expectParseException("Spaces", "    ");
        expectParseException("Spaces newline", "      \n");
        expectParseException("Lots of whitespace", "  \n\t\r   \t \n \t \n ");
        expectParseException("Empty group", "()");
    }


    @Test
    /**
     * Test comments.
     *
     * Comments are indicated by a # symbol anywhere on a line.
     * Everything up to the end of that line is regarded as comment.
     *
     * Comments should be stripped out by the parser, leaving
     * nothing to produce a ParseTree with.  In turn this should
     * cause a ParseException to be thrown.
     *
     * Note: still unsure about throwing a ParseException when the
     * syntax of the expression is perfectly valid.  It's just that
     * it results in a null ParseTree if that is all there is.
     */
    public final void testComments() {
        expectParseException("Single line comment", "#Just a comment");
        expectParseException("Comment with new line", "#Just a comment\n");
        expectParseException("Comments on several lines", "\n    #Just a comment  \n\r  #Another comment\n");
        expectParseException("Comments no ending new line", "\n    #Just a comment  \n\r \t #Another comment");
    }


    @Test
    /**
     * Test ParseTreeType.BYTE
     *
     * Byte nodes contain a single byte value and have no child nodes.
     *
     * They are parsed from a two-digit hex string, with any case allowed.
     * Whitespace is not allowed between the hex digits.
     */
    public final void testByte() throws ParseException {
        expectParseException("Spaced hex", " a a");
        expectParseException("Newline hex", "0\n1");
        expectParseException("Spaced digits", "0 1");
        expectParseException("Single digit", "0");
        expectParseException("Space single digit ", " 1");
        expectParseException("Tabs single hex", "\t\ta");
        expectParseException("Illegal hex char", "1g");
        expectParseException("Whitespace and illegal hex chars", " xy\t");
        expectParseException("space between hex value and id", "0x 01");

        testByte("01", (byte) 0x01, true);
        testByte("  01  ", (byte) 0x01, true);
        testByte("  01  # 0x01", (byte) 0x01, true);
        testByte("FF", (byte) 0xFF, true);
        testByte("\tFF", (byte) 0xFF, true);
        testByte("00", (byte) 0x00, true);
        testByte("\n\r00", (byte) 0x00, true);
        testByte("cd", (byte) 0xcd, true);
        testByte("cd\t \n", (byte) 0xcd, true);
        testByte("d4", (byte) 0xD4, true);
        testByte(" \t   d4\t   ", (byte) 0xd4, true);
        testByte("fe", (byte) 0xfe, true);
        testByte("fe   \r\t\n", (byte) 0xFE, true);
        testByte("fe   \r\t\n # a comment", (byte) 0xFE, true);
        testByte("7e", (byte) 0x7e, true);
        testByte("7e            ", (byte) 0x7e, true);
        testByte("# a comment\ndd\t ", (byte) 0xdd, true);

        testByte("0x01", (byte) 0x01, true);
        testByte("  0x01  ", (byte) 0x01, true);
        testByte("  0x01  # 0x01", (byte) 0x01, true);
        testByte("0xFF", (byte) 0xFF, true);
        testByte("\t0xFF", (byte) 0xFF, true);
        testByte("0x00", (byte) 0x00, true);
        testByte("\n\r0x00", (byte) 0x00, true);
        testByte("0xcd", (byte) 0xcd, true);
        testByte("0xcd\t \n", (byte) 0xcd, true);
        testByte("0xd4", (byte) 0xD4, true);
        testByte(" \t   0xd4\t   ", (byte) 0xd4, true);
        testByte("0xfe", (byte) 0xfe, true);
        testByte("0xfe   \r\t\n", (byte) 0xFE, true);
        testByte("0xfe   \r\t\n # a comment", (byte) 0xFE, true);
        testByte("0x7e", (byte) 0x7e, true);
        testByte("0x7e            ", (byte) 0x7e, true);

        testByte("0i00000001", (byte) 0x01, true);
        testByte("  0i00000001  ", (byte) 0x01, true);
        testByte("  0i00000001  # 0x01", (byte) 0x01, true);
        testByte("0i11111111", (byte) 0xFF, true);
        testByte("\t0i11111111", (byte) 0xFF, true);
        testByte("0i00000000", (byte) 0x00, true);
        testByte("\n\r0i00000000", (byte) 0x00, true);
        testByte("0i11001101", (byte) 0xcd, true);
        testByte("0i11001101\t \n", (byte) 0xcd, true);
        testByte("0i11010100", (byte) 0xD4, true);
        testByte(" \t   0i11010100\t   ", (byte) 0xd4, true);
        testByte("0i11111110", (byte) 0xfe, true);
        testByte("0i11111110   \r\t\n", (byte) 0xFE, true);
        testByte("0i11111110   \r\t\n # a comment", (byte) 0xFE, true);
        testByte("0i01111110", (byte) 0x7e, true);
    }

    private void testByte(String expression, byte value, boolean canInvert) throws ParseException {
        testByte(parser.parse(expression), value, false);
        if (canInvert) {
            testByte(parser.parse('^' + expression), value, true);
        } else {
            expectParseException("Expression can't be inverted by prepending ^", '^' + expression);
        }
    }


    private void testByte(ParseTree node, byte value, boolean isInverted) throws ParseException {
        assertEquals("Node [" + node + "] has type BYTE",
                ParseTreeType.BYTE, node.getParseTreeType());
        testByteValue(node, value, isInverted);
    }

    private void testByteValue(ParseTree node, byte value, boolean isInverted) throws ParseException {
        assertEquals("Node " + node + " has byte value " + value,
                value, node.getByteValue());
        assertEquals("Node " + node + " inversion should be " + isInverted, isInverted, node.isValueInverted());
        assertEquals("Node " + node + " has no children",
                0, node.getNumChildren());
    }

    @Test
    /**
     * Test ParseTreeType.STRING
     */
    public final void testString() throws ParseException {
        expectParseException("Unclosed string", "'a string");
        expectParseException("Unopened string", "abc'");
        expectParseException("Unopened string", "An unopened'");
        expectParseException("Mixed case quotes", "'Closed with case insensitive`");
        expectParseException("Inverted string", "^'An inverted string'");
        expectParseException("Empty string", "''");

        testByte("' '", (byte) ' ', true);
        testByte("'X'", (byte) 'X', true);
        testByte("'0'", (byte) '0', true);

        testString("'one two three four'");
        testString("'some words\nwith a new line'");
    }

    private void testString(String expression) throws ParseException {
        testString(parser.parse(expression), stripQuotes(expression));
    }

    private void testString(ParseTree node, String value) throws ParseException {
        assertEquals("Node " + node + " type is ParseTreeType.STRING",
                ParseTreeType.STRING, node.getParseTreeType());
        assertEquals("Node " + node + " value is: " + value,
                value, node.getTextValue());
        assertEquals("Node " + node + " has no children",
                0, node.getNumChildren());
    }


    @Test
    /**
     * Test ParseTreeType.CASE_INSENSITIVE_STRING
     */
    public final void testCaseInsensitiveString() throws ParseException {
        expectParseException("Unclosed string", "`a string");
        expectParseException("Unopened string", "abc`");
        expectParseException("Unopened string", "An unopened`");
        expectParseException("Mixed case quotes", "`Closed with case sensitive'");
        expectParseException("Inverted case insensitive string", "^`Inverted case insensitive string`");
        expectParseException("Empty string", "``");

        testCaseInsensitiveString("` `");
        testCaseInsensitiveString("`q`");
        testCaseInsensitiveString("`7`");
        testCaseInsensitiveString("`one two three four`");
        testCaseInsensitiveString("`some words\nwith a new line`");
    }

    private void testCaseInsensitiveString(String expression) throws ParseException {
        testCaseInsensitiveString(parser.parse(expression), stripQuotes(expression));
    }

    private void testCaseInsensitiveString(ParseTree node, String value) throws ParseException {
        assertEquals("Node " + node + " has type ParseTreeType.CASE_INSENSITIVE_STRING",
                ParseTreeType.CASE_INSENSITIVE_STRING, node.getParseTreeType());
        assertEquals("Node " + node + " has value: " + value,
                value, node.getTextValue());
        assertEquals("Node " + node + " has no children",
                0, node.getNumChildren());
    }


    @Test
    /*
	 * Test ParseTreeType.ANY
	 */
    public void testAny() throws ParseException {
        testAny(".");
        testAny("   .   ");
        testAny("(.)");
        testAny(" (.) ");
    }

    private void testAny(String expression) throws ParseException {
        testAny(parser.parse(expression));
    }

    private void testAny(ParseTree node) {
        assertEquals("Node " + node + " has ParseTreeType.ANY",
                ParseTreeType.ANY, node.getParseTreeType());
        assertFalse("Node " + node + " is not inverted", node.isValueInverted());
        assertEquals("Node " + node + " has zero children",
                0, node.getNumChildren());
    }


    @Test
	/* 
	 * Test ParseTreeType.SET
	 */
    public void testSetOfBytes() throws ParseException {
        expectParseException("Empty set", "[]");
        expectParseException("Unclosed set", "[01 02 03");
        expectParseException("Opening bracket in set", "[01 (]");

        testSetOfBytes("[01]", new byte[]{(byte) 0x01}, true);
        testSetOfBytes("[ 01 ]", new byte[]{(byte) 0x01}, true);
        testSetOfBytes("[0102]", new byte[]{(byte) 0x02, (byte) 0x01}, true);
        testSetOfBytes("[0201]", new byte[]{(byte) 0x02, (byte) 0x01}, true);
        testSetOfBytes("[0201^03]", new byte[]{(byte) 0x02, (byte) 0x01, (byte) 0x03}, true);
        testSetOfBytes("[02 01 03]", new byte[]{(byte) 0x02, (byte) 0x01, (byte) 0x03}, true);
        testSetOfBytes("\t\r[0201]", new byte[]{(byte) 0x02, (byte) 0x01}, false);
        testSetOfBytes("   \n  [0201^03]", new byte[]{(byte) 0x02, (byte) 0x01, (byte) 0x03}, false);
    }


    @Test
    public void testSetOfRanges() throws ParseException {
        expectParseException("Unfinished range inside set", "[00-]");
        expectParseException("Unstarted range inside set", "[-99]");

        testSetOfRanges("[00-ff]", false, 0, 255);
        testSetOfRanges("[ 00-20  80-C0  ]", false, 0, 32, 128, 192);
        testSetOfRanges("[ de-ad 'a'-'z'] ", false, 0xde, 0xad, 'a', 'z');
        testSetOfRanges("[ 00-01 04-05 07-0A '0'-'9' ab-ad 1d-ea    ]", false, 0, 1, 4, 5, 7, 10, '0', '9', 0xab, 0xad, 0x1d, 0xea);
    }

    private void testSetOfRanges(String expression, boolean inverted, int... rangeValues) throws ParseException {
        testSetOfRanges(parser.parse(expression), false, inverted, rangeValues);
        testSetOfRanges(parser.parse('^' + expression), true, inverted, rangeValues);
    }

    private void testSetOfRanges(ParseTree node, boolean setInverted, boolean rangesInverted, int... rangeValues) throws ParseException {
        assertEquals("Node type is SET", ParseTreeType.SET, node.getParseTreeType());
        assertEquals("Node is correct inversion: " + setInverted, setInverted, node.isValueInverted());
        int i = 0;
        for (ParseTree range : node) {
            assertEquals("First child of node has correct value " + rangeValues[i], (byte) rangeValues[i], range.getChild(0).getByteValue());
            assertEquals("Second child of node has correct value " + rangeValues[i + 1], (byte) rangeValues[i + 1], range.getChild(1).getByteValue());
            i += 2;
        }
    }


    @Test
    public void testSetOfStrings() throws ParseException {
        expectParseException("Unclosed string inside set", "['9]");
        expectParseException("Unopened string inside set", "[9']");

        testSetOfStrings("['99']", "99");
        testSetOfStrings("['abc' 'def' '012']", "abc", "def", "012");
        testSetOfStrings("['I knowe a banke' '24680' ' \t any \n other \r whitespace?']", "I knowe a banke", "24680", " \t any \n other \r whitespace?");
    }

    private void testSetOfStrings(String expression, String... values) throws ParseException {
        testSetOfStrings(parser.parse(expression), false, values);
        testSetOfStrings(parser.parse('^' + expression), true, values);
    }

    private void testSetOfStrings(ParseTree node, boolean inverted, String... values) throws ParseException {
        assertEquals("Node type is set " + node, ParseTreeType.SET, node.getParseTreeType());
        assertEquals("Node has correct inversion " + inverted, inverted, node.isValueInverted());
        int childIndex = 0;
        for (String value : values) {
            ParseTree childNode = node.getChild(childIndex++);
            assertEquals("Child node is a string " + childNode, ParseTreeType.STRING, childNode.getParseTreeType());
            assertEquals("Child node " + childIndex + " has correct value " + value, value, childNode.getTextValue());
        }
    }

    @Test
    public void testSetOfCaseStrings() throws ParseException {
        expectParseException("Unclosed case string inside set", "[`9]");
        expectParseException("Unopened case string inside set", "[9`]");

        testSetOfCaseStrings("[`9`]", "9");
        testSetOfCaseStrings("[`abc` `def` `012`]", "abc", "def", "012");
        testSetOfCaseStrings("[`I knowe a banke` `24680` ` \t any \n other \r whitespace?`]", "I knowe a banke", "24680", " \t any \n other \r whitespace?");
    }

    private void testSetOfCaseStrings(String expression, String... values) throws ParseException {
        testSetOfCaseStrings(parser.parse(expression), false, values);
        testSetOfCaseStrings(parser.parse('^' + expression), true, values);
    }

    private void testSetOfCaseStrings(ParseTree node, boolean inverted, String... values) throws ParseException {
        assertEquals("Node type is set " + node, ParseTreeType.SET, node.getParseTreeType());
        assertEquals("Node has correct inversion " + inverted, inverted, node.isValueInverted());
        int childIndex = 0;
        for (String value : values) {
            ParseTree childNode = node.getChild(childIndex++);
            assertEquals("Child node is a case insensitive string " + childNode, ParseTreeType.CASE_INSENSITIVE_STRING, childNode.getParseTreeType());
            assertEquals("Child node " + childIndex + " has correct value " + value, value, childNode.getTextValue());
        }
    }


    @Test
    public void testNestedSets() throws ParseException {
        expectParseException("unclosed nested set", "[01 02 [03 04]");

        testNestedSet("two simple sets 1", "[01 02 [03 04]]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, false);
        testNestedSet("two simple sets 2", "[[03 04] 01 02]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, false);
        testNestedSet("two simple sets 3", "[01 [03 04] 02]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, false);
        testNestedSet("two simple sets 4", "[01 02 ^[03 04]]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, true);
        testNestedSet("two simple sets 5", "[^[03 04] 01 02]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, true);
        testNestedSet("two simple sets 6", "[01^[03 04] 02]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, true);

        testNestedSet("two overlapping sets 1", "[01 02 [02 04]]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x02, (byte) 0x04}, false);
        testNestedSet("two overlapping sets 2", "[[02 04] 01 02]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x02, (byte) 0x04}, false);
        testNestedSet("two overlapping sets 3", "[01 [03 04] 04]",
                new byte[]{(byte) 0x01, (byte) 0x04}, new byte[]{(byte) 0x03, (byte) 0x04}, false);
        testNestedSet("two overlapping sets 4", "[01 02 ^[02 02]]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x02}, true);
        testNestedSet("two overlapping sets 5", "[^[03 04] 01 02]",
                new byte[]{(byte) 0x01, (byte) 0x02}, new byte[]{(byte) 0x03, (byte) 0x04}, true);
        testNestedSet("two overlapping sets 6", "[01^[01 01] 01]",
                new byte[]{(byte) 0x01}, new byte[]{(byte) 0x01}, true);

    }

    private void testNestedSet(String description, String setDefinition,
                               byte[] directValues, byte[] nestedValues,
                               boolean nestedSetInverted) throws ParseException {
        testNestedSet(description, parser.parse(setDefinition), false, directValues, nestedValues, nestedSetInverted);
        testNestedSet(description + " inverted", parser.parse("^" + setDefinition), true, directValues, nestedValues, nestedSetInverted);
    }

    private void testNestedSet(String description, ParseTree set, boolean inverted,
                               byte[] directValues, byte[] nestedValues, boolean nestedSetInverted) throws ParseException {
        assertEquals("Node is a set", ParseTreeType.SET, set.getParseTreeType());
        assertEquals("Inversion status is correct", inverted, set.isValueInverted());
        int childIndex = ParseTreeUtils.getChildIndexOfType(set, 0, ParseTreeType.SET);
        assertTrue("There is a child set of the parent set", childIndex >= 0);
        ParseTree nestedSet = set.getChild(childIndex);
        assertEquals("Node is a set", ParseTreeType.SET, nestedSet.getParseTreeType());
        assertEquals("Inversion status is correct", nestedSetInverted, nestedSet.isValueInverted());

        Set<Byte> expected = ParseTreeUtils.getSetValues(nestedSet);
        Set<Byte> result = ByteUtils.toSet(nestedValues);
        assertEquals("Bytes are correct for the nested set", expected, result);
    }


    private void testSetOfBytes(String expression, byte[] values, boolean canInvert) throws ParseException {
        testSetOfBytes(parser.parse(expression), false, values);
        if (canInvert) {
            testSetOfBytes(parser.parse("^" + expression), true, values);
        }
    }

    private void testSetOfBytes(ParseTree node, boolean isInverted, byte[] values) throws ParseException {
        assertEquals("Node " + node + " has ParseTreeType.SET",
                ParseTreeType.SET, node.getParseTreeType());
        assertEquals("Node " + node + " inversion is " + isInverted, isInverted, node.isValueInverted());
        testSetByteValues(node, values);
    }

    private void testSetByteValues(ParseTree node, byte[] values) throws ParseException {
        Set<Byte> nodeVals = new HashSet<Byte>();
        for (ParseTree child : node) {
            nodeVals.add(child.getByteValue());
        }
        Set<Byte> vals = ByteUtils.toSet(values);
        assertEquals("Sets have the same number of values", nodeVals.size(), vals.size());
        nodeVals.removeAll(vals);
        assertEquals("Sets have the same values", 0, nodeVals.size());
    }


    @Test
    /**
     * Test simple sequences of nodes containing a single byte value.
     */
    public void testByteSequence() throws ParseException {
        expectParseException("Sequence with partial value", "00 01 0");
        expectParseException("Sequence with partial value", "00 0 02");

        byte[] values = ByteUtils.toArray((byte) 0, (byte) 1);
        testByteSequence("0001", values);
        testByteSequence("00 01", values);
        testByteSequence("00\n\t01", values);
        testByteSequence("00 # zero byte\n\t01\t# one byte", values);

        values = ByteUtils.toArray((byte) 0xca, (byte) 0xfe, (byte) 0xbe, (byte) 0xef);
        testByteSequence("Cafebeef", values);
        testByteSequence("Ca fe be ef", values);
        testByteSequence("\nCa\tfe  \t  \rbe ef", values);

        values = ByteUtils.toArray((byte) 0x00, (byte) 0x7f, (byte) 0x45);
        testByteSequence(" 00 7f 45", values);

        values = ByteUtils.toArray((byte) 0x09, (byte) 0x0a, (byte) 0x0b,
                (byte) 0x0c, (byte) 0x0d, (byte) 0x1e);
        testByteSequence(" \\t \\n \\v \\f \\r \\e", values);

        for (int testNumber = 0; testNumber < 50; testNumber++) {
            testRandomByteSequence();
        }
    }

    private void testByteSequence(String expression, byte[] values) throws ParseException {
        testByteSequence(parser.parse(expression), values);
    }

    private void testByteSequence(ParseTree node, byte[] values) throws ParseException {
        assertEquals("Node " + node + " has type ParseTreeType.SEQUENCE",
                ParseTreeType.SEQUENCE, node.getParseTreeType());
        assertEquals("Node " + node + " has " + values.length + " children nodes",
                values.length, node.getNumChildren());
        testByteSequenceValues(node, values);
    }

    private void testRandomByteSequence() throws ParseException {
        // a sequence has to have at least two elements.
        int length = random.nextInt(100) + 2;
        byte[] values = new byte[length];
        StringBuilder builder = new StringBuilder(length * 2);
        for (int index = 0; index < length; index++) {
            values[index] = (byte) (random.nextInt(256));
            builder.append(hexString(values[index]));
        }
        testByteSequence(builder.toString(), values);
    }

    private void testByteSequenceValues(ParseTree sequence, byte[] values) throws ParseException {
        int position = 0;
        for (ParseTree member : sequence) {
            assertEquals("Byte sequence node position " + position + " value is " + values[position],
                    values[position++], member.getByteValue());
        }
    }

    /**
     * Test shorthand byte definitions.
     */

    @Test
    public void testShorthand() throws ParseException {
        expectParseException("unknown shorthand", "\\k");

        testSingleByteShorthand("TAB", "\\t", (byte) 0x09);
        testSingleByteShorthand("NEWLINE", "\\n", (byte) 0x0A);
        testSingleByteShorthand("CARRIAGE RETURN", "\\r", (byte) 0x0d);
        testSingleByteShorthand("VERTICAL TAB", "\\v", (byte) 0x0b);
        testSingleByteShorthand("FORM FEED", "\\f", (byte) 0x0c);
        testSingleByteShorthand("ESCAPE", "\\e", (byte) 0x1e);

        testRangeShorthand("DIGITS", "\\d", '0', '9', false);
        testRangeShorthand("NOT DIGITS", "\\D", '0', '9', true);
        testRangeShorthand("LOWERCASE", "\\l", 'a', 'z', false);
        testRangeShorthand("NOT LOWERCASE", "\\L", 'a', 'z', true);
        testRangeShorthand("UPPERCASE", "\\u", 'A', 'Z', false);
        testRangeShorthand("NOT UPPERCASE", "\\U", 'A', 'Z', true);
        testRangeShorthand("ASCII", "\\i", 0, 127, false);
        testRangeShorthand("NOT ASCII", "\\I", 0, 127, true);

        testSetShorthand("WORD CHARS", "\\w", false, '0', '9', 'a', 'z', 'A', 'Z', '_', '_');
        testSetShorthand("NOT WORD CHARS", "\\W", true, '0', '9', 'a', 'z', 'A', 'Z', '_', '_');
        testSetShorthand("WHITESPACE", "\\s", false, 32, 32, 9, 9, 10, 10, 13, 13);
        testSetShorthand("NOT WHITESPACE", "\\S", true, 32, 32, 9, 9, 10, 10, 13, 13);
    }

    private void testSingleByteShorthand(String description, String expression, byte value) throws ParseException {
        testSingleByteShorthand(description, parser.parse(expression), false, value);
        testSingleByteShorthand(description, parser.parse('^' + expression), true, value);
        testSingleByteShorthand(description, parser.parse('[' + expression + ']'), false, value);
        testSingleByteShorthand(description, parser.parse("^[" + expression + ']'), false, value);
        testSingleByteShorthand(description, parser.parse("[^" + expression + ']'), true, value);
    }

    private void testSingleByteShorthand(String description, ParseTree node, boolean inverted, byte value) throws ParseException {
        if (node.getParseTreeType() == ParseTreeType.SET) {
            node = node.getChild(0);
        }
        assertEquals(description + " Node is correctly inverted " + node, inverted, node.isValueInverted());
        assertEquals(description + " Node is a byte " + node, ParseTreeType.BYTE, node.getParseTreeType());
        assertEquals(description + " Node has correct value " + node, value, node.getByteValue());
    }

    private void testRangeShorthand(String description, String expression, int rangeStart, int rangeEnd, boolean rangeInverted) throws ParseException {
        testRangeShorthand(description, parser.parse(expression), rangeStart, rangeEnd, rangeInverted);
        testRangeShorthand(description, parser.parse('^' + expression), rangeStart, rangeEnd, !rangeInverted);
        testRangeShorthand(description, parser.parse('[' + expression + ']'), rangeStart, rangeEnd, rangeInverted);
        testRangeShorthand(description, parser.parse("^[" + expression + ']'), rangeStart, rangeEnd, rangeInverted);
        testRangeShorthand(description, parser.parse("[^" + expression + ']'), rangeStart, rangeEnd, !rangeInverted);
    }

    private void testRangeShorthand(String description, ParseTree node, int rangeStart, int rangeEnd, boolean rangeInverted) throws ParseException {
        if (node.getParseTreeType() == ParseTreeType.SET) {
            node = node.getChild(0); // Get the range inside the set.
        }
        assertEquals(description + " Node is correctly inverted " + node, rangeInverted, node.isValueInverted());
        assertEquals(description + " Node is a range " + node, ParseTreeType.RANGE, node.getParseTreeType());
        assertEquals(description + " Node has correct first value " + rangeStart, rangeStart, node.getChild(0).getIntValue());
        assertEquals(description + " Node has correct second value " + rangeEnd, rangeEnd, node.getChild(1).getIntValue());
    }

    private void testSetShorthand(String description, String expression, boolean inverted, int... rangeValues) throws ParseException {
        testSetShorthand(description, parser.parse(expression), inverted, rangeValues);
        testSetShorthand(description, parser.parse('^' + expression), !inverted, rangeValues);
        //testSetShorthand(description, parser.parse('[' + expression + ']'), inverted, rangeValues);
        //testSetShorthand(description, parser.parse("^[" + expression + ']'), inverted, rangeValues);
        //testSetShorthand(description, parser.parse("[^" + expression + ']'), !inverted, rangeValues);
    }

    private void testSetShorthand(String description, ParseTree node, boolean inverted, int... rangeValues) throws ParseException {
        assertEquals(description + " Node is a set " + node, ParseTreeType.SET, node.getParseTreeType());
        assertEquals(description + " Node is correctly inverted " + node, inverted, node.isValueInverted());
        int childIndex = 0;
        for (int i = 0; i < rangeValues.length; i += 2) {
            int firstValue = rangeValues[i];
            int secondValue = rangeValues[i + 1];
            if (firstValue == secondValue) {
                testSingleByteShorthand(description, node.getChild(childIndex), false, (byte) firstValue);
            } else {
                testRangeShorthand(description, node.getChild(childIndex), firstValue, secondValue, false);
            }
            childIndex++;
        }
    }


    @Test
    /**
     * Test ParseTreeType.RANGE
     */
    public void testRange() throws ParseException {
        expectParseException("Unclosed range hex byte", "01-");
        expectParseException("Unclosed range hex byte with a any bitmask following", "01- ~fe");
        expectParseException("Unclosed range hex byte with a group opened", "01- ('abcde'|ff");
        expectParseException("Unclosed range character", "'q'- ");
        expectParseException("Unclosed range bad comment", "01-#bad comment 02");
        expectParseException("More than one char", "'aa'-'f'");
        expectParseException("More than one char", "01-'more'");
        expectParseException("Inverted second range value", "01-^02");

        testRange("01-02", (byte) 0x01, (byte) 0x02);
        testRange("01 - 02", (byte) 0x01, (byte) 0x02);
        testRange("01\n-\n02", (byte) 0x01, (byte) 0x02);
        testRange("'a'-'z'", (byte) 'a', (byte) 'z');
        testRange("'a'-ff", (byte) 'a', (byte) 0xff);
        testRange("ff-'a'", (byte) 0xff, (byte) 'a');
        testRange("\\t-\\r", (byte) 0x09, (byte) 0x0d);

        testRangeSequence("01-0203", (byte) 0x01, (byte) 0x02, 0);
        testRangeSequence("0201 - 02", (byte) 0x01, (byte) 0x02, 1);
        testRangeSequence("0201\n-\n0201", (byte) 0x01, (byte) 0x02, 1);
        testRangeSequence("'word1' 'word2' 'a'-'z' 02 03", (byte) 'a', (byte) 'z', 2);
        testRangeSequence("01 02 03 04 'a'-ff", (byte) 'a', (byte) 0xff, 4);
        testRangeSequence("fe'fe'ff-'a'023f", (byte) 0xff, (byte) 'a', 2);

        testRangeSet("[01-02]", (byte) 0x01, (byte) 0x02, 0);
        testRangeSet("[ 01 01 - 02 ]", (byte) 0x01, (byte) 0x02, 1);
        testRangeSet("[01\n-\n02 03]", (byte) 0x01, (byte) 0x02, 0);
        testRangeSet("['xyz' 'a'-'z' '123']", (byte) 'a', (byte) 'z', 1);
        testRangeSet("['a' 'f' 'a'-ff]", (byte) 'a', (byte) 0xff, 2);
        testRangeSet("[ff-'a']", (byte) 0xff, (byte) 'a', 0);
    }

    private void testRange(String expression, byte value1, byte value2) throws ParseException {
        testRange(parser.parse(expression), value1, value2);
        testRange(parser.parse('^' + expression), value1, value2);
    }

    private void testRangeSequence(String expression, byte value1, byte value2, int index) throws ParseException {
        final ParseTree sequenceRangeValue = parser.parse(expression).getChild(index);
        testRange(sequenceRangeValue, value1, value2);
    }

    private void testRangeSet(String expression, byte value1, byte value2, int index) throws ParseException {
        final ParseTree setRangeValue = parser.parse(expression).getChild(index);
        testRange(setRangeValue, value1, value2);
    }

    private void testRange(ParseTree rangeNode, byte value1, byte value2) throws ParseException {
        assertEquals("Node " + rangeNode + " has type ParseTreeType.RANGE",
                ParseTreeType.RANGE, rangeNode.getParseTreeType());
        testRangeValueNode(rangeNode.getChild(0), value1);
        testRangeValueNode(rangeNode.getChild(1), value2);
    }

    private void testRangeValueNode(ParseTree rangeValueNode, byte value) throws ParseException {
        if (rangeValueNode.getParseTreeType() == ParseTreeType.BYTE) {
            assertEquals("Node " + rangeValueNode + " has byte value " + value,
                    value, rangeValueNode.getByteValue());
        } else {
            fail("Range value node was not a byte " + rangeValueNode);
        }
    }

    @Test
    /**
     * Test ParseTreeType.ZERO_TO_MANY
     */
    public void testZeroToMany() throws ParseException {
        expectParseException("nothing to quantify", "*");
        expectParseException("double many", "01**");

        testByte(testZeroToMany("01*"), (byte) 1, false);
        testByte(testZeroToMany("(01)*"), (byte) 1, false);
        testByte(testZeroToMany("(01 *)"), (byte) 1, false);
        testByte(testZeroToMany("^01*"), (byte) 1, true);
        testByte(testZeroToMany("(^01)*"), (byte) 1, true);
        testByte(testZeroToMany("(^01 *)"), (byte) 1, true);

        testAny(testZeroToMany(".*"));
        testAny(testZeroToMany("(.)*"));

        testString(testZeroToMany("'a string'*"), "a string");
        testString(testZeroToMany("( ' X ')*"), " X ");

        testCaseInsensitiveString(testZeroToMany("`abcdefghijklmnopqrstuvwxyz`*"), "abcdefghijklmnopqrstuvwxyz");
        testCaseInsensitiveString(testZeroToMany("  (`   `)    *"), "   ");

        testByteSequence(testZeroToMany("(01 02 03)*"),
                new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});
        testByteSequence(testZeroToMany("(ce ff 00 97 32 22)*"),
                new byte[]{(byte) 0xce, (byte) 0xff, (byte) 0x00,
                        (byte) 0x97, (byte) 0x32, (byte) 0x22});

    }

    private ParseTree testZeroToMany(String expression) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.ZERO_TO_MANY",
                ParseTreeType.ZERO_TO_MANY, node.getParseTreeType());
        assertEquals("Expression " + expression + " has one child node",
                1, node.getNumChildren());
        return node.getChild(0);
    }


    @Test
    /**
     * Test ParseTreeType.ONE_TO_MANY
     */
    public void testOneToMany() throws ParseException {
        expectParseException("nothing to quantify", "+");
        expectParseException("double many", "01++");

        testByte(testOneToMany("01+"), (byte) 1, false);
        testByte(testOneToMany("(01)+"), (byte) 1, false);
        testByte(testOneToMany("(01 +)"), (byte) 1, false);
        testByte(testOneToMany("^01+"), (byte) 1, true);
        testByte(testOneToMany("(^01)+"), (byte) 1, true);
        testByte(testOneToMany("(^01 +)"), (byte) 1, true);

        testAny(testOneToMany(".+"));
        testAny(testOneToMany("(.)+"));

        testString(testOneToMany("'a string'+"), "a string");
        testString(testOneToMany("( ' Y ')+"), " Y ");

        testCaseInsensitiveString(testOneToMany("`abcdefghijklmnopqrstuvwxyz`+"), "abcdefghijklmnopqrstuvwxyz");
        testCaseInsensitiveString(testOneToMany("  (`   `)  #comment\n  +"), "   ");

        testByteSequence(testOneToMany("(01 02 03)+"),
                new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});
        testByteSequence(testOneToMany("(ce ff 00 97 32 22)+"),
                new byte[]{(byte) 0xce, (byte) 0xff, (byte) 0x00,
                        (byte) 0x97, (byte) 0x32, (byte) 0x22});
    }

    private ParseTree testOneToMany(String expression) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.ONE_TO_MANY",
                ParseTreeType.ONE_TO_MANY, node.getParseTreeType());
        assertEquals("Expression " + expression + " has one child node",
                1, node.getNumChildren());
        return node.getChild(0);
    }


    @Test
    /**
     * Test ParseTreeType.OPTIONAL
     */
    public void testOptional() throws ParseException {
        expectParseException("nothing to make optional", "?");
        expectParseException("double optional", "01??");

        testByte(testOptional("01?"), (byte) 1, false);
        testByte(testOptional("(01)?"), (byte) 1, false);
        testByte(testOptional("(01 ?)"), (byte) 1, false);
        testByte(testOptional("^01?"), (byte) 1, true);
        testByte(testOptional("(^01)?"), (byte) 1, true);
        testByte(testOptional("(^01 ?)"), (byte) 1, true);

        testAny(testOptional(".?"));
        testAny(testOptional("(.)?"));

        testString(testOptional("'a string'?"), "a string");
        testString(testOptional("( ' Z ')?"), " Z ");

        testCaseInsensitiveString(testOptional("`abcdefghijklmnopqrstuvwxyz`?"), "abcdefghijklmnopqrstuvwxyz");
        testCaseInsensitiveString(testOptional("  (`   `)  #comment\n  ?"), "   ");

        testByteSequence(testOptional("(01 02 03)?"),
                new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03});
        testByteSequence(testOptional("(ce ff 00 97 32 22)?"),
                new byte[]{(byte) 0xce, (byte) 0xff, (byte) 0x00,
                        (byte) 0x97, (byte) 0x32, (byte) 0x22});
    }

    private ParseTree testOptional(String expression) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.OPTIONAL",
                ParseTreeType.OPTIONAL, node.getParseTreeType());
        assertEquals("Expression " + expression + " has one child node",
                1, node.getNumChildren());
        return node.getChild(0);
    }


    @Test
    /**
     * Test ParseTreeType.REPEAT
     */
    public void testRepeat() throws ParseException {
        expectParseException("Nothing to repeat", "{4}");
        expectParseException("Empty repeat", "ab{}");
        expectParseException("Zero repeat", "00{0}");
        expectParseException("Unclosed repeat", "fe{99 ");
        expectParseException("Unopened repeat", "fe 99}");
        expectParseException("Whitespace inside repeat", "fe{ 99}");
        expectParseException("Whitespace inside repeat", "fe{99 }");

        testByte(testRepeats("01{3}", 3), (byte) 0x01, false);
        testByte(testRepeats("  01  \n  {3}", 3), (byte) 0x01, false);
        testByte(testRepeats("^01{3}", 3), (byte) 0x01, true);
        testByte(testRepeats("  ^01  \n  {3}", 3), (byte) 0x01, true);

        testString(testRepeats("'Titania'{5}", 5), "Titania");
        testString(testRepeats("'Titania'   {5}", 5), "Titania");

        testCaseInsensitiveString(testRepeats("`html`{45}", 45), "html");
        testCaseInsensitiveString(testRepeats("`html`\r{45}", 45), "html");

        testByteSequence(testRepeats("(01 02 03){32}", 32), new byte[]{(byte) 1, (byte) 2, (byte) 3});
        testByteSequence(testRepeats(" ( 01 02 03 )   {32} ", 32), new byte[]{(byte) 1, (byte) 2, (byte) 3});

        testAny(testRepeats(".{3}", 3));
        testAny(testRepeats(".   {3}", 3));
        testAny(testRepeats("( . )   {67}", 67));
    }

    private ParseTree testRepeats(String expression, int repeats) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.REPEAT",
                ParseTreeType.REPEAT, node.getParseTreeType());
        assertEquals("Expression " + expression + " has two child nodes",
                2, node.getNumChildren());
        assertEquals("Expression " + expression + " has repeat value " + repeats,
                repeats, node.getChild(0).getIntValue());
        return node.getChild(1);
    }


    @Test
    /**
     * Test ParseTreeType.REPEAT_MIN_TO_MANY
     */
    public void testRepeatMinToMany() throws ParseException {
        expectParseException("Nothing to repeat", "{99,*}");
        expectParseException("Unclosed repeat", "fe{99,* ");
        expectParseException("Unopened repeat", "fe 99,*}");
        expectParseException("Whitespace inside repeat", "fe{ 99,*}");
        expectParseException("Whitespace inside repeat", "fe{99,* }");
        expectParseException("Whitespace inside repeat", "fe{99 , *}");

        testByte(testRepeatMinToMany("01{3,*}", 3), (byte) 0x01, false);
        testByte(testRepeatMinToMany("  01  \n  {3,*}", 3), (byte) 0x01, false);
        testByte(testRepeatMinToMany("^01{3,*}", 3), (byte) 0x01, true);
        testByte(testRepeatMinToMany("  ^01  \n  {3,*}", 3), (byte) 0x01, true);


        testString(testRepeatMinToMany("'Titania'{5,*}", 5), "Titania");
        testString(testRepeatMinToMany("'Titania'   {5,*}", 5), "Titania");

        testCaseInsensitiveString(testRepeatMinToMany("`html`{45,*}", 45), "html");
        testCaseInsensitiveString(testRepeatMinToMany("`html`\r{45,*}", 45), "html");

        testByteSequence(testRepeatMinToMany("(01 02 03){32,*}", 32), new byte[]{(byte) 1, (byte) 2, (byte) 3});
        testByteSequence(testRepeatMinToMany(" ( 01 02 03 )   {32,*} ", 32), new byte[]{(byte) 1, (byte) 2, (byte) 3});

        testAny(testRepeatMinToMany(".{3,*}", 3));
        testAny(testRepeatMinToMany(".   {3,*}", 3));
        testAny(testRepeatMinToMany("( . )   {67,*}", 67));
    }

    private ParseTree testRepeatMinToMany(String expression, int repeats) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.REPEAT_MIN_TO_MANY",
                ParseTreeType.REPEAT_MIN_TO_MANY, node.getParseTreeType());
        assertEquals("Expression " + expression + " has two child nodes",
                2, node.getNumChildren());
        assertEquals("Expression " + expression + " has repeat value " + repeats,
                repeats, node.getChild(0).getIntValue());
        return node.getChild(1);
    }


    @Test
    /**
     * Test ParseTreeType.REPEAT_MIN_TO_MAX
     */
    public void testRepeatMinToMax() throws ParseException {
        expectParseException("Nothing to repeat", "{99,100}");
        expectParseException("Unclosed repeat", "fe{99,100 ");
        expectParseException("Unopened repeat", "fe 99,100}");
        expectParseException("Whitespace inside repeat", "fe{ 99,98}");
        expectParseException("Whitespace inside repeat", "fe{99,100 }");
        expectParseException("Whitespace inside repeat", "fe{99 , 45}");

        testByte(testRepeatMinToMax("01{3,6}", 3, 6), (byte) 0x01, false);
        testByte(testRepeatMinToMax("  01  \n  {3,5}", 3, 5), (byte) 0x01, false);
        testByte(testRepeatMinToMax("^01{3,6}", 3, 6), (byte) 0x01, true);
        testByte(testRepeatMinToMax("  ^01  \n  {3,5}", 3, 5), (byte) 0x01, true);

        testString(testRepeatMinToMax("'Titania'{5,5}", 5, 5), "Titania");
        testString(testRepeatMinToMax("'Titania'   {5,0}", 5, 0), "Titania");

        testCaseInsensitiveString(testRepeatMinToMax("`html`{45,3}", 45, 3), "html");
        testCaseInsensitiveString(testRepeatMinToMax("`html`\r{45,54}", 45, 54), "html");

        testByteSequence(testRepeatMinToMax("(01 02 03){32,987}", 32, 987), new byte[]{(byte) 1, (byte) 2, (byte) 3});
        testByteSequence(testRepeatMinToMax(" ( 01 02 03 )   {32,23} ", 32, 23), new byte[]{(byte) 1, (byte) 2, (byte) 3});

        testAny(testRepeatMinToMax(".{3,6}", 3, 6));
        testAny(testRepeatMinToMax(".   {3,10}", 3, 10));
        testAny(testRepeatMinToMax("( . )   {67,256}", 67, 256));
    }

    private ParseTree testRepeatMinToMax(String expression, int minRepeat, int maxRepeat) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.REPEAT_MIN_TO_MAX",
                ParseTreeType.REPEAT_MIN_TO_MAX, node.getParseTreeType());
        assertEquals("Expression " + expression + " has three child nodes",
                3, node.getNumChildren());
        assertEquals("Expression " + expression + " has min repeat value " + minRepeat,
                minRepeat, node.getChild(0).getIntValue());
        assertEquals("Expression " + expression + " has max repeat value " + minRepeat,
                maxRepeat, node.getChild(1).getIntValue());
        return node.getChild(2);
    }


    @Test
    /**
     * Test ParseTreeType.ALTERNATIVES
     */
    public void testAlternatives() throws ParseException {
        expectParseException("No left alternative", "   |02");
        expectParseException("No right alternative", "01|   ");
        expectParseException("            | ", "blank spaces");

        // Note that all alternatives consisting of a match on a single
        // byte position are optimised into a set type.

        byte[] values0 = ByteUtils.toArray((byte) 0x00, (byte) 0x01);
        testSetOfBytes("00|01", values0, false);
        testSetOfBytes("01|00", values0, false);
        testSetOfBytes("(01|00)", values0, false);
        testSetOfBytes("('\u0001'|'\u0000')", values0, false);

        byte[] values1 = ByteUtils.toArray((byte) 0x00);
        byte[] values3 = ByteUtils.toArray((byte) 0x7f, (byte) 0x7f, (byte) 0x80, (byte) 0xff);
        testByteSequenceAlternatives("00|01|7f 7f 80 ff", values3, values0);
        testByteSequenceAlternatives("7f 7f 80 ff|01|00", values3, values0);
        testByteSequenceAlternatives("'\u007f' '\u007f' '\u0080' ff|01|00", values3, values0);
        testByteSequenceAlternatives("(7f 7f 80 ff|01|00)", values3, values0);

        byte[] values4 = ByteUtils.toArray((byte) 0xde, (byte) 0xad, (byte) 0xff);
        testByteSequenceAlternatives("00|deadff|7f 7f 80 ff", values1, values4, values3);
        testByteSequenceAlternatives("deadff|00|7f 7f 80 ff|01|00", values4, values3, values0);
        testByteSequenceAlternatives("(00|deadff|7f 7f 80 ff)", values1, values4, values3);
        testByteSequenceAlternatives("(deadff|00|7f 7f 80 ff|01|00)", values4, values3, values0);

        byte[] values5 = ByteUtils.toArray((byte) 0x32, (byte) 0x01);
        byte[] values6 = ByteUtils.toArray((byte) 0x6c, (byte) 0xff, (byte) 0xee, (byte) 0xdd);
        testByteSequenceAlternatives("deadff|32|  6cffeedd|01", values4, values6, values5);
        testByteSequenceAlternatives("(deadff|32|  6cffeedd|01)", values4, values6, values5);
    }

    private void testByteSequenceAlternatives(String expression, byte[]... values) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals("Expression " + expression + " has type ParseTreeType.ALTERNATIVES",
                ParseTreeType.ALTERNATIVES, node.getParseTreeType());
        assertEquals("Expression " + expression + " has " + values.length + " children nodes",
                values.length, node.getNumChildren());
        testByteSequenceAlternativesValues(node, values);
    }

    private void testByteSequenceAlternativesValues(ParseTree alternatives, byte[][] values) throws ParseException {
        int position = 0;
        for (ParseTree alternative : alternatives) {
            byte[] value = values[position++];
            if (alternative.getParseTreeType() == ParseTreeType.SEQUENCE) {
                testByteSequenceValues(alternative, value);
            } else if (alternative.getNumChildren() == 0) {
                testByteValue(alternative, value[0], false);
            } else {
                testSetByteValues(alternative, value);
                //throw new ParseException("Not a sequence but has children: [" + alternative + "]");
            }
        }
    }


    /**
     * Tests alternatives optimisation, which looks for single-byte alternatives and merges them into a single
     * set.
     */
    @Test
    public void testAlternativesOptimisation() throws ParseException {
        testAlternativesOptimisation("All bytes in a set", "'0'|'1'|'2'|'3'|'4'|'5'|'6'",
                new byte[]{(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
                        (byte) '5', (byte) '6'});
        testAlternativesOptimisation("One string not in set", "'012'|'A'|'b'|01",
                new byte[]{(byte) 'A', (byte) 'b', (byte) 0x01});
        testAlternativesOptimisation("No optimisation if only one value is length 1", "'AB'|'0'|'CDEF'|'YZ'",
                new byte[0]);
    }

    @SuppressWarnings("null")
    private void testAlternativesOptimisation(String description, String expression, byte[] setValues) throws ParseException {
        ParseTree result = parser.parse(expression);
        ParseTree set = null;
        if (result.getParseTreeType() == ParseTreeType.SET) {
            set = result;
        } else {
            for (int childIndex = 0; childIndex < result.getNumChildren(); childIndex++) {
                if (result.getChild(childIndex).getParseTreeType() == ParseTreeType.SET) {
                    set = result.getChild(childIndex);
                    break;
                }
            }
        }
        if (setValues.length > 0) {
            assertTrue("There must be a set for alternatives expression " + expression, set != null);
            assertFalse("The set is not inverted for expression " + expression, set.isValueInverted());
            Set<Byte> setBytes = ParseTreeUtils.calculateSetValues(set);
            assertEquals("Number of bytes in the set for expression " + expression + " is correct", setValues.length, setBytes.size());
            for (int byteIndex = 0; byteIndex < setValues.length; byteIndex++) {
                byte value = setValues[byteIndex];
                assertTrue("Set contains the byte value " + value, setBytes.contains(value));
            }
        } else {
            assertNull("There is no set", set);
        }
    }


    @Test
    /**
     * Test Groups ()
     */
    public void testGroups() throws ParseException {
        expectParseException("Empty group", "()");
        expectParseException("Empty group with whitespace", "(    )");
        expectParseException("Empty group comments and whitespace", " (  #open\n \t  ) ");

        testByte("  (01)", (byte) 1, false);
        testByte(" ((01))", (byte) 1, false);
        testByte("(((01)))", (byte) 1, false);

        byte[] values1 = ByteUtils.toArray((byte) 1, (byte) 2, (byte) 3);
        testByteSequence(" (01) (02) (03)", values1);
        testByteSequence("  01  (02)  03", values1);
        testByteSequence(" (01   02   03)", values1);
        testByteSequence("((01) (02)  03)", values1);
        testByteSequence("((01) (02) (03))", values1);
        testByteSequence("((01)  02  (03))", values1);
    }

    @Test
    public void testNestedGroups() throws ParseException {
        byte[] values1 = ByteUtils.toArray((byte) 2, (byte) 3);

        testNestedByteSequence("Simple nested sequence", "(01 (02 03))", values1);
        testNestedByteSequence("Simple nested sequence 2", "((02 03) 01)", values1);

        testQuantifiedNestedByteSequence("Quantified nested sequence 1", "(01 (02 03)+)", ParseTreeType.ONE_TO_MANY, values1);
        testQuantifiedNestedByteSequence("Quantified nested sequence 2", "((02 03)* 01)", ParseTreeType.ZERO_TO_MANY, values1);

        // embedded alternatives:
        testNestedAlternatives("Simple nested alternatives 1", "01 | ('ab' 01 02 |'cd'|'ef' ff fe) | 03 05");
        testNestedAlternatives("Simple nested alternatives 2", "'one two three' | ('one'|'two'|'three') | 03 05+");
    }

    private void testNestedByteSequence(String description, String expression, byte[] nestedSequence) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals(description + " parent node is a sequence", ParseTreeType.SEQUENCE, node.getParseTreeType());
        int childIndex = ParseTreeUtils.getChildIndexOfType(node, 0, ParseTreeType.SEQUENCE);
        assertTrue(description + " parent node contains a child sequence", childIndex >= 0);
        ParseTree childSequence = node.getChild(childIndex);
        testByteSequence(childSequence, nestedSequence);
    }

    private void testQuantifiedNestedByteSequence(String description, String expression, ParseTreeType quantifier, byte[] nestedSequence) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals(description + " parent node is a sequence", ParseTreeType.SEQUENCE, node.getParseTreeType());
        int childIndex = ParseTreeUtils.getChildIndexOfType(node, 0, quantifier);
        assertTrue(description + " parent node contains a quantifier node", childIndex >= 0);
        ParseTree childSequence = ParseTreeUtils.getFirstChild(node.getChild(childIndex));
        testByteSequence(childSequence, nestedSequence);
    }

    private void testNestedAlternatives(String description, String expression) throws ParseException {
        ParseTree node = parser.parse(expression);
        assertEquals(description + " parent node are alternatives", ParseTreeType.ALTERNATIVES, node.getParseTreeType());
        int childIndex = ParseTreeUtils.getChildIndexOfType(node, 0, ParseTreeType.ALTERNATIVES);
        assertTrue(description + " parent node contains an alternatives node", childIndex >= 0);
    }

    @Test
    /**
     * Test complicated expressions with lots of different moving parts which should parse correctly,
     * but for which we won't validate that the AST itself is correct (too complex to bother testing all nodes).
     */
    public void testComplexExpressions() throws ParseException {
        parser.parse("((01)* 02 03 'a string')? ^[20-40 ^[45-6a 5f]]");
        parser.parse("((('abc'? deff){1,3}|'abcd')+|01 ^02 03 ' '|\\d");
        parser.parse("(((01+ 02)* | ('any old' 55 02 `bytes here`){1,100}) | ^[01 02 ^~55 ^[^~AA]])");
        //FEATURE: Possibly create a random expression generator to really exercise the parser.
    }
	
	
	/*
	 * Utility methods
	 */


    private String hexString(int value) {
        final String hexString = Integer.toString(value & 0xFF, 16);
        return hexString.length() == 1 ? '0' + hexString : hexString;
    }

    private String stripQuotes(String fromString) {
        return fromString.substring(1, fromString.length() - 1);
    }


    private void expectParseException(String description, String expression) {
        try {
            parser.parse(expression);
            fail(description + " with expression " + expression + " was expected to generate a ParseException");
        } catch (ParseException expected) {
        }
    }

    @Test(expected = ParseException.class)
    public void testIllegalCharsetEncoding() throws Exception {
        parser.parse("(*NOT-A-VALID-CHARSET)");
    }

    @Test(expected = ParseException.class)
    public void testUnclosedCharsetEncoding() throws Exception {
        parser.parse("(*UTF-8 'the encoding should have been terminated with a round bracket.'");
    }

    @Test(expected = ParseException.class)
    public void testAdditionalSpaceAfterBracketCharsetEncoding() throws Exception {
        parser.parse("( *UTF-8) 'makes no sense if there is space between bracket and *'");
    }

    @Test
    public void testCharSetEncodings() throws Exception {
        testCharSetEncodings("'encoded in default ISO 8859-1'", Charset.forName("ISO-8859-1"));
        testCharSetEncodings("(*UTF-8)'encoded in UTF-8'", Charset.forName("UTF-8"));
        testCharSetEncodings("(*UTF-16BE)'encoded in UTF_16BE'", Charset.forName("UTF-16BE"));
    }

    private void testCharSetEncodings(String expression, Charset charset) throws Exception {
        ParseTree node = parser.parse(expression);
        assertEquals(node.toString(), ParseTreeType.STRING, node.getParseTreeType());
        assertEquals(node.toString(), charset, node.getTextEncoding());
    }

    @Test
    public void testMultipleCharsetEncodings() throws Exception {
        // No spaces used.
        testCharSetMultiEncodings("(*UTF-8,UTF-16BE)'encoded in UTF_8 and UTF_16BE'",
                Charset.forName("UTF-8"), Charset.forName("UTF-16BE"));

        // One space
        testCharSetMultiEncodings("(*UTF-8, UTF-16BE)'encoded in UTF_8 and UTF_16BE'",
                Charset.forName("UTF-8"), Charset.forName("UTF-16BE"));

        // Many spaces
        testCharSetMultiEncodings("(*UTF-8,        UTF-16BE        )'encoded in UTF_8 and UTF_16BE'",
                Charset.forName("UTF-8"), Charset.forName("UTF-16BE"));

    }

    private void testCharSetMultiEncodings(String expression, Charset... charsets) throws Exception {
        ParseTree node = parser.parse(expression);

        // Now an alternatives node.
        assertEquals(node.toString(), ParseTreeType.ALTERNATIVES, node.getParseTreeType());

        // check they are all string children with the right charset encoding.
        int charsetIndex = 0;
        for (ParseTree stringNode : node) {
            assertEquals(stringNode.toString(), ParseTreeType.STRING, stringNode.getParseTreeType());
            assertEquals(charsets[charsetIndex++], stringNode.getTextEncoding());
        }
    }


    @Test
    public void testCharSetEncodingsInSets() throws Exception {
        testCharSetEncodingsInSets("['encoded in default ISO 8859-1']", Charset.forName("ISO-8859-1"));
        testCharSetEncodingsInSets("[(*UTF-8)'encoded in UTF-8']", Charset.forName("UTF-8"));
        testCharSetEncodingsInSets("[(*UTF-16BE)'encoded in UTF_16BE']", Charset.forName("UTF-16BE"));
    }

    private void testCharSetEncodingsInSets(String expression, Charset charset) throws Exception {
        ParseTree node = parser.parse(expression);
        assertEquals(node.toString(), ParseTreeType.SET, node.getParseTreeType());
        assertEquals(node.toString(), 1, node.getNumChildren());

        node = node.getChild(0);
        assertEquals(node.toString(), ParseTreeType.STRING, node.getParseTreeType());
        assertEquals(node.toString(), StandardCharsets.ISO_8859_1, node.getTextEncoding());
    }

    @Test
    public void testParseWildByte() throws Exception {
        testParseWildByte("_A", 0x0A, 0x0F);
        testParseWildByte("0x_A", 0x0A, 0x0F);

        testParseWildByte("A_", 0xA0, 0xF0);
        testParseWildByte("0xA_", 0xA0, 0xF0);

        testParseWildByte("__", 0x00, 0x00);
        testParseWildByte("0x__", 0x00, 0x00);

        testParseWildByte("0i_______1", 0x01, 0x01);
        testParseWildByte("0i_______0", 0x00, 0x01);
        testParseWildByte("0i1111____", 0xF0, 0xF0);
        testParseWildByte("0i0000____", 0x00, 0xF0);

        testParseWildByte("0i____1001", 0x09, 0x0F);
        testParseWildByte("0i____1010", 0x0A, 0x0F);
    }

    private void testParseWildByte(String expression, int value, int mask) throws Exception {
        ParseTree node = parser.parse(expression);
        assertEquals(ParseTreeType.WILDBIT, node.getParseTreeType());
        ParseTree maskNode = node.getChild(0);
        ParseTree valueNode = node.getChild(1);
        assertEquals(expression, (byte) mask, maskNode.getByteValue());
        assertEquals(expression, (byte) value, valueNode.getByteValue());
    }

    @Test
    public void testIllegalCharWildBits() throws Exception {
        testIllegalCharWildBits("0x_G");
        testIllegalCharWildBits("_X");
        testIllegalCharWildBits("0xx_");

        testIllegalCharWildBits("0i000000O1"); // Capital O, not zero.
        testIllegalCharWildBits("0i_______O"); // Capital O, not zero.
        testIllegalCharWildBits("0i__0__i_O"); // Capital O, not zero.
    }

    private void testIllegalCharWildBits(String expression) {
        try {
            parser.parse(expression);
            fail("Should have obtained a ParseException for: " + expression);
        } catch (ParseException expectedDoNothing) {}
    }

    @Test
    public void testAnyBits() throws Exception {
        testAnyBits("~_A", 0x0A, 0x0F);
        testAnyBits("~0x_A",  0x0A, 0x0F);
        testAnyBits("~0i____1010",  0x0A, 0x0F);

        testAnyBits("~A_", 0xA0, 0xF0);
        testAnyBits("~0xA_", 0xA0, 0xF0);

        testAnyBits("~__", 0x00, 0x00);
        testAnyBits("~0x__", 0x00, 0x00);

        testAnyBits("~0i_______1", 0x01, 0x01);
        testAnyBits("~0i_______0", 0x00, 0x01);
        testAnyBits("~0i1111____", 0xF0, 0xF0);
        testAnyBits("~0i0000____", 0x00, 0xF0);

        testAnyBits("~0i____1001", 0x09, 0x0F);
        testAnyBits("~0i____1010", 0x0A, 0x0F);
    }

    private void testAnyBits(String expression, int value, int mask) throws Exception {
        ParseTree node = parser.parse(expression);
        assertEquals(ParseTreeType.ANYBITS, node.getParseTreeType());
        assertEquals(2, node.getNumChildren());
        ParseTree valueNode = node.getChild(1);
        ParseTree maskNode = node.getChild(0);
        assertEquals(expression, (byte) value, valueNode.getByteValue());
        assertEquals(expression, (byte) mask, maskNode.getByteValue());
    }

    @Test
    public void testInvalidAnyBitsNode() throws Exception {
        expectParseException("any bits and a range", "~23-24");
        expectParseException("any bits and a set", "~[01 02 04]");
        expectParseException("any bits and a string", "~'ab'");
        expectParseException("any bits and a case insensitive string", "~`ab`");
        expectParseException("any bits and an inverted byte", "~^01");
        expectParseException("any bits and an inverted wildbyte", "~^_1");
        expectParseException("any bits and an inverted range", "~^21-AF");
    }

    @Test
    public void testBuildRange() throws Exception {
        for (int test = 0; test < 256; test++) {
            int minValue = random.nextInt(256);
            int maxValue = random.nextInt(256);
            boolean isInverted = random.nextBoolean();
            testBuildRange((byte) minValue, (byte) maxValue, isInverted);
        }
    }

    private void testBuildRange(byte minValue, byte maxValue, boolean isInverted) throws ParseException {
        ParseTree node = RegexParser.buildRange(minValue, maxValue, isInverted);
        assertEquals(node.toString(), ParseTreeType.RANGE, node.getParseTreeType());
        assertEquals(node.toString(), isInverted, node.isValueInverted());
        assertEquals("num children = 2", 2, node.getNumChildren());
        ParseTree firstValue = node.getChild(0);
        ParseTree secondValue = node.getChild(1);
        assertEquals(minValue, firstValue.getByteValue());
        assertEquals(maxValue, secondValue.getByteValue());
    }

    @Test
    public void testInvalidRangeNodes() throws Exception {
        expectParseException("inverted second byte", "21-^40");
        expectParseException("inverted all bytes", "^31-^7e");

        expectParseException("first any byte", "~A1-A3");
        expectParseException("second any byte", "~1F-7E");
        expectParseException("all any bytes", "~1F-~7E");

        expectParseException("first value a set", "[01-02]-03");
        expectParseException("second value a set", "01-[01 02]");
        expectParseException("all sets", "[0102]-[0304]");

        expectParseException("first value a wildbit", "0_-1F");
        expectParseException("second value a wildbit", "01-_1");
        expectParseException("all values wildbits", "7_-0i0010_10");

        expectParseException("first a long string", "'ab'-'c'");
        expectParseException("second a long string", "01-'gh'");
        expectParseException("all a string", "'ab'-'cd'");

        expectParseException("first a case string", "`a`-'c'");
        expectParseException("second a case string", "01-`d`");
        expectParseException("all a case string", "`a`-`d`");
    }



    @Test
    public void testBuildSet() throws Exception {
        testBuildSet("01");
        testBuildSet("0102030405060708090A0B0C0D0E0F10");
        testBuildSet("~01^34 22-6A 0i_1_0_0_1");
    }


    private void testBuildSet(String setMemberSequence) throws Exception {
        // Get an array of ParseTrees for the sequence passed in:
        ParseTree[] children = getSequenceAsArray(setMemberSequence);

        ParseTree set = RegexParser.buildSet(children);
        testBuildSet(set, children, false);

        set = RegexParser.buildInvertedSet(children);
        testBuildSet(set, children, true);
    }

    private ParseTree[] getSequenceAsArray(String expression) throws ParseException {
        ParseTree sequence = parser.parse(expression);
        final int numChildren = sequence.getNumChildren();
        ParseTree[] children = new ParseTree[numChildren];
        for (int index = 0; index < numChildren; index++) {
            children[index] = sequence.getChild(index);
        }
        return children;
    }

    private void testBuildSet(ParseTree set, ParseTree[] members, boolean isInverted) throws ParseException {
        final int numChildren = set.getNumChildren();
        assertEquals(ParseTreeType.SET, set.getParseTreeType());
        assertEquals(isInverted, set.isValueInverted());
        assertEquals(numChildren, set.getNumChildren());
        for (int index = 0; index < numChildren; index++) {
            checkEqual(members[index], set.getChild(index));
        }
    }

    private void checkEqual(ParseTree expected, ParseTree actual) throws ParseException {
        // Could implement equals on parse tree nodes...
        assertEquals(expected.getParseTreeType(), actual.getParseTreeType());
        switch (expected.getParseTreeType()) {
            case BYTE: {
                assertEquals(expected.getByteValue(), actual.getByteValue());
                break;
            }
            case STRING:
            case CASE_INSENSITIVE_STRING: {
                assertEquals(expected.getTextValue(), actual.getTextValue());
                break;
            }
        }
        assertEquals(expected.getNumChildren(), actual.getNumChildren());
        for (int child = 0; child < expected.getNumChildren(); child++) {
            checkEqual(expected.getChild(child), actual.getChild(child));
        }
    }

    @Test
    public void testCommentsInSets() throws Exception {
        testCommentsInSets("01 02 03 04");
        testCommentsInSets("~0i0000___1 34 6A 00 FF");
    }

    private void testCommentsInSets(String sequenceExpression) throws Exception {
        ParseTree[] sequence = getSequenceAsArray(sequenceExpression);

        String setExpression = "[ #comment\n" + sequenceExpression + "# and another\n ]";
        try {
            ParseTree node = parser.parse(setExpression);
            testBuildSet(node, sequence, false);

            node = parser.parse("^" + setExpression);
            testBuildSet(node, sequence, true);
        } catch (ParseException problem) {
            fail("Should not have a parse exception with comments in set definitions.");
        }
    }

    @Test
    public void testInvalidCharInSet() throws Exception {
        expectParseException(" non byte char", "[01 02 Y4]");
        expectParseException(" typo", "[01 02 £]");
        expectParseException(" open group in set", "[01 02 03 (04 05 06) 07 08 09]");
    }

    @Test
    public void testAnyInSet() throws ParseException {
        ParseTree node = parser.parse("[.]");
        assertEquals(ParseTreeType.SET, node.getParseTreeType());
        assertEquals(1, node.getNumChildren());
        ParseTree any = node.getChild(0);
        assertEquals(ParseTreeType.ANY, any.getParseTreeType());
    }

}
