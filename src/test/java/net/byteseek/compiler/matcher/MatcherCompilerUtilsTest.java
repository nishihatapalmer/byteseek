/*
 * Copyright Matt Palmer 2009, All rights reserved.
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
package net.byteseek.compiler.matcher;

import net.byteseek.matcher.bytes.*;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.parser.ParseException;
import net.byteseek.parser.regex.RegexParser;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class MatcherCompilerUtilsTest {

    static Random random = new Random();


    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = ?
        random.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }

    @Test
    public void testConstruction() {
        new MatcherCompilerUtils(); // does nothing - it only has static utilities.
    }

    @Test
    public void testIsInverted() throws Exception {
        ByteNode node = new ByteNode((byte) 0, false);
        assertFalse(MatcherCompilerUtils.isInverted(node, false));
        assertTrue(MatcherCompilerUtils.isInverted(node, true));

        node = new ByteNode((byte) 0, true);
        assertTrue(MatcherCompilerUtils.isInverted(node, false));
        assertFalse(MatcherCompilerUtils.isInverted(node, true));
    }

    @Test
    public void testCreateByteMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            ByteNode node = new ByteNode((byte) i);
            ByteMatcher matcher = MatcherCompilerUtils.createByteMatcher(node);
            assertEquals("Class is a OneByteMatcher", OneByteMatcher.class, matcher.getClass());
            assertEquals("Matches only one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("Value is " + i, i, matcher.getMatchingBytes()[0] & 0xFF);

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createByteMatcher(node);
            assertEquals("Class is an OneByteInvertedMatcher", OneByteInvertedMatcher.class, matcher.getClass());
            assertEquals("Matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());
            byte[] matching = matcher.getMatchingBytes();
            for (byte by : matching) {
                if (((int) by & 0xFF) == i) {
                    fail("The byte value " + i + " was found in the inverted matcher.");
                }
            }
        }
    }

    @Test
    public void testCreateByteMatcherWithInversion() throws Exception {
        for (int i = 0; i < 256; i++) {
            ByteNode node = new ByteNode((byte) i, false);
            ByteMatcher matcher = MatcherCompilerUtils.createByteMatcher(node, false);
            assertEquals("Class is a OneByteMatcher", OneByteMatcher.class, matcher.getClass());
            assertEquals("Matches only one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("Value is " + i, i, matcher.getMatchingBytes()[0] & 0xFF);

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createByteMatcher(node, false);
            assertEquals("Class is an OneByteInvertedMatcher", OneByteInvertedMatcher.class, matcher.getClass());
            assertEquals("Matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());
            byte[] matching = matcher.getMatchingBytes();
            for (byte by : matching) {
                if (((int) by & 0xFF) == i) {
                    fail("The byte value " + i + " was found in the inverted matcher.");
                }
            }

            node = new ByteNode((byte) i, false);
            matcher = MatcherCompilerUtils.createByteMatcher(node, true);
            assertEquals("Class is an OneByteInvertedMatcher", OneByteInvertedMatcher.class, matcher.getClass());
            assertEquals("Matches 255 bytes", 255, matcher.getNumberOfMatchingBytes());
            matching = matcher.getMatchingBytes();
            for (byte by : matching) {
                if (((int) by & 0xFF) == i) {
                    fail("The byte value " + i + " was found in the inverted matcher.");
                }
            }

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createByteMatcher(node, true);
            assertEquals("Class is a OneByteMatcher", OneByteMatcher.class, matcher.getClass());
            assertEquals("Matches only one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("Value is " + i, i, matcher.getMatchingBytes()[0] & 0xFF);
        }
    }

    @Test
    public void testCreateAnyMatcher() throws Exception {
        ParseTree node = new ByteNode((byte) 0);
        ByteMatcher matcher = MatcherCompilerUtils.createAnyMatcher(node);
        assertEquals("Matcher is an any matcher", AnyByteMatcher.class, matcher.getClass());

        try {
            node = new ByteNode((byte) 0, true);
            MatcherCompilerUtils.createAnyMatcher(node);
            fail("Expected a ParseException if attempting to compile an inverted node into an Any Matcher.");
        } catch (ParseException expectedIgnore) {}
    }

    @Test
    public void testCreateAnyMatcherInversion() throws Exception {
        ParseTree node = new ByteNode((byte) 0);
        ByteMatcher matcher = MatcherCompilerUtils.createAnyMatcher(node, false);
        assertEquals("Matcher is an any matcher", AnyByteMatcher.class, matcher.getClass());

        try {
            MatcherCompilerUtils.createAnyMatcher(node, true);
            fail("Expected a ParseException if attempting to compile an inverted node into an Any Matcher.");
        } catch (ParseException expectedIgnore) {}

        node = new ByteNode((byte) 0, true);
        matcher = MatcherCompilerUtils.createAnyMatcher(node, true);
        assertEquals("Matcher is an any matcher", AnyByteMatcher.class, matcher.getClass());

        try {
            MatcherCompilerUtils.createAnyMatcher(node, false);
            fail("Expected a ParseException if attempting to compile an inverted node into an Any Matcher.");
        } catch (ParseException expectedIgnore) {}
    }

    @Test
    public void testCreateAllBitmaskMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            ParseTree node = new ByteNode((byte) i);
            ByteMatcher matcher = MatcherCompilerUtils.createAllBitmaskMatcher(node);
            assertEquals("Matcher is an all bitmask matcher", AllBitmaskMatcher.class, matcher.getClass());
            int numMatched = matcher.getNumberOfMatchingBytes();

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createAllBitmaskMatcher(node);
            assertEquals("Matcher is an all bitmask matcher", AllBitmaskMatcher.class, matcher.getClass());

            assertEquals("Both matchers match 256 bytes together", 256, numMatched + matcher.getNumberOfMatchingBytes());
        }
    }

    @Test
    public void testCreateAllBitmaskMatcherInversion() throws Exception {
        for (int i = 0; i < 256; i++) {
            ParseTree node = new ByteNode((byte) i);
            ByteMatcher matcher = MatcherCompilerUtils.createAllBitmaskMatcher(node, false);
            assertEquals("Matcher is an all bitmask matcher", AllBitmaskMatcher.class, matcher.getClass());
            int numMatchedNotInverted = matcher.getNumberOfMatchingBytes();

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createAllBitmaskMatcher(node, false);
            assertEquals("Matcher is an all bitmask matcher", AllBitmaskMatcher.class, matcher.getClass());
            int numMatchedInverted = matcher.getNumberOfMatchingBytes();

            assertEquals("Both matchers match 256 bytes together", 256, numMatchedNotInverted + numMatchedInverted);

            node = new ByteNode((byte) i);
            matcher = MatcherCompilerUtils.createAllBitmaskMatcher(node, true);
            assertEquals("Matcher is an all bitmask matcher", AllBitmaskMatcher.class, matcher.getClass());
            assertEquals("Matcher matches inverted matcher", numMatchedInverted, matcher.getNumberOfMatchingBytes());

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createAllBitmaskMatcher(node, true);
            assertEquals("Matcher is an all bitmask matcher", AllBitmaskMatcher.class, matcher.getClass());
            numMatchedNotInverted = matcher.getNumberOfMatchingBytes();
            assertEquals("Both matchers match 256 bytes together", 256, numMatchedInverted + numMatchedNotInverted);
        }
    }

    @Test
    public void testCreateAnyBitmaskMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            ParseTree node = new ByteNode((byte) i);
            ByteMatcher matcher = MatcherCompilerUtils.createAnyBitmaskMatcher(node);
            assertEquals("Matcher is an all bitmask matcher", AnyBitmaskMatcher.class, matcher.getClass());
            int numMatched = matcher.getNumberOfMatchingBytes();

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createAnyBitmaskMatcher(node);
            assertEquals("Matcher is an all bitmask matcher", AnyBitmaskMatcher.class, matcher.getClass());

            assertEquals("Both matchers match 256 bytes together", 256, numMatched + matcher.getNumberOfMatchingBytes());
        }
    }

    @Test
    public void testCreateAnyBitmaskMatcherInversion() throws Exception {
        for (int i = 0; i < 256; i++) {
            ParseTree node = new ByteNode((byte) i);
            ByteMatcher matcher = MatcherCompilerUtils.createAnyBitmaskMatcher(node, false);
            assertEquals("Matcher is an any bitmask matcher", AnyBitmaskMatcher.class, matcher.getClass());
            int numMatchedNotInverted = matcher.getNumberOfMatchingBytes();

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createAnyBitmaskMatcher(node, false);
            assertEquals("Matcher is an any bitmask matcher", AnyBitmaskMatcher.class, matcher.getClass());
            int numMatchedInverted = matcher.getNumberOfMatchingBytes();

            assertEquals("Both matchers match 256 bytes together", 256, numMatchedNotInverted + numMatchedInverted);

            node = new ByteNode((byte) i);
            matcher = MatcherCompilerUtils.createAnyBitmaskMatcher(node, true);
            assertEquals("Matcher is an any bitmask matcher", AnyBitmaskMatcher.class, matcher.getClass());
            assertEquals("Matcher matches inverted matcher", numMatchedInverted, matcher.getNumberOfMatchingBytes());

            node = new ByteNode((byte) i, true);
            matcher = MatcherCompilerUtils.createAnyBitmaskMatcher(node, true);
            assertEquals("Matcher is an any bitmask matcher", AnyBitmaskMatcher.class, matcher.getClass());
            numMatchedNotInverted = matcher.getNumberOfMatchingBytes();
            assertEquals("Both matchers match 256 bytes together", 256, numMatchedInverted + numMatchedNotInverted);
        }
    }

    @Test
    public void testCreateRangeMatcher() throws Exception {
        for (int i = 0; i < 256; i++) {
            int first = random.nextInt(256);
            int second = random.nextInt(256);
            testRangeAndInvertedRange(first, second);
        }
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeType() throws ParseException {
        ParseTree badNode = new ByteNode((byte) 0);
        MatcherCompilerUtils.createRangeMatcher(badNode);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeTypeInverted() throws ParseException {
        ParseTree badNode = new ByteNode((byte) 0);
        MatcherCompilerUtils.createRangeMatcher(badNode, true);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeNoChildren() throws ParseException {
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE);
        MatcherCompilerUtils.createRangeMatcher(badNode);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeNoChildrenInverted() throws ParseException {
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE);
        MatcherCompilerUtils.createRangeMatcher(badNode, true);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeOneChild() throws ParseException {
        ParseTree firstNode  = new ByteNode((byte) 0);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE, children);
        MatcherCompilerUtils.createRangeMatcher(badNode);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeOneChildInverted() throws ParseException {
        ParseTree firstNode  = new ByteNode((byte) 0);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE, children);
        MatcherCompilerUtils.createRangeMatcher(badNode, true);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeThreeChild() throws ParseException {
        ParseTree firstNode  = new ByteNode((byte) 0);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        children.add(firstNode);
        children.add(firstNode);
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE, children);
        MatcherCompilerUtils.createRangeMatcher(badNode);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeThreeChildInverted() throws ParseException {
        ParseTree firstNode  = new ByteNode((byte) 0);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        children.add(firstNode);
        children.add(firstNode);
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE, children);
        MatcherCompilerUtils.createRangeMatcher(badNode, true);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeWrongChildType() throws ParseException {
        ParseTree firstNode  = new IntNode(0);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        children.add(firstNode);
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE, children);
        MatcherCompilerUtils.createRangeMatcher(badNode);
    }

    @Test(expected = ParseException.class)
    public void testNotCorrectRangeWrongChildTypeInverted() throws ParseException {
        ParseTree firstNode  = new IntNode(0);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        children.add(firstNode);
        ParseTree badNode = new ChildrenNode(ParseTreeType.RANGE, children);
        MatcherCompilerUtils.createRangeMatcher(badNode, true);
    }


    private void testRangeAndInvertedRange(int first, int second) throws Exception {
        ParseTree firstNode  = new ByteNode((byte) first);
        ParseTree secondNode = new ByteNode((byte) second);
        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(firstNode);
        children.add(secondNode);
        ParseTree rangeNode  = new ChildrenNode(ParseTreeType.RANGE, children);

        //Should all succeed without throwing an exception.
        testRangeMatcher(first, second, false, MatcherCompilerUtils.createRangeMatcher(rangeNode));
        testRangeMatcher(first, second, false, MatcherCompilerUtils.createRangeMatcher(rangeNode, false));
        testRangeMatcher(first, second, true, MatcherCompilerUtils.createRangeMatcher(rangeNode, true));
        rangeNode = new ChildrenNode(ParseTreeType.RANGE, children, true);
        testRangeMatcher(first, second, true, MatcherCompilerUtils.createRangeMatcher(rangeNode, false));
        testRangeMatcher(first, second, false, MatcherCompilerUtils.createRangeMatcher(rangeNode, true));
    }

    private void testRangeMatcher(int first, int second, boolean inverted, ByteMatcher rangeMatcher) {
        assertEquals("Type is a range matcher", ByteRangeMatcher.class, rangeMatcher.getClass());
        int numInRange = first < second? second - first + 1 : first - second + 1;
        int expectedMatch = inverted? 256-numInRange : numInRange;
        assertEquals("Matches correct number of bytes", expectedMatch, rangeMatcher.getNumberOfMatchingBytes());
    }

    @Test
    public void testCreateByteMatchersFromSet() throws Exception {
        ParseTree byteNode = new ByteNode((byte) 0);
        ParseTree inverted = new ByteNode((byte) 0, true);
        testSingleSetValue(byteNode, inverted, OneByteMatcher.class, OneByteInvertedMatcher.class, 1);

        // Put this in a nested set - same results expected:
        byteNode  = new ChildrenNode(ParseTreeType.SET, byteNode);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted);
        testSingleSetValue(byteNode, inverted, OneByteMatcher.class, OneByteInvertedMatcher.class, 1);

        // Put in an inverted nested set - opposite results expected:
        byteNode  = new ChildrenNode(ParseTreeType.SET, byteNode, true);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted, true);
        testSingleSetValue(inverted, byteNode, OneByteMatcher.class, OneByteInvertedMatcher.class, 1);
    }

    @Test
    public void testCreateRangeMatchersFromSet() throws Exception {
        ParseTree rangeNode = RegexParser.buildRange((byte) 20, (byte) 127, false);
        ParseTree inverted  = RegexParser.buildRange((byte) 20, (byte) 127, true);
        testSingleSetValue(rangeNode, inverted, ByteRangeMatcher.class, ByteRangeMatcher.class, 108);

        // Put this in a nested set - same results expected:
        rangeNode  = new ChildrenNode(ParseTreeType.SET, rangeNode);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted);
        testSingleSetValue(rangeNode, inverted, ByteRangeMatcher.class, ByteRangeMatcher.class, 108);

        // Put in an inverted nested set - opposite results expected:
        rangeNode  = new ChildrenNode(ParseTreeType.SET, rangeNode, true);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted, true);
        testSingleSetValue(inverted, rangeNode, ByteRangeMatcher.class, ByteRangeMatcher.class, 108);
    }

    @Test
    public void testCreateWildbitMatchersFromSet() throws Exception {
        ParseTree wildBitNode = new ChildrenNode(ParseTreeType.WILDBIT, new ByteNode((byte) 0x53), new ByteNode((byte) 0x72));
        ParseTree inverted    = new ChildrenNode(ParseTreeType.WILDBIT, true, new ByteNode((byte) 0x53), new ByteNode((byte) 0x72));
        testSingleSetValue(wildBitNode, inverted, WildBitMatcher.class, WildBitMatcher.class, 16);

        wildBitNode  = new ChildrenNode(ParseTreeType.SET, wildBitNode);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted);
        testSingleSetValue(wildBitNode, inverted, WildBitMatcher.class, WildBitMatcher.class, 16);

        // Put in an inverted nested set - opposite results expected:
        wildBitNode  = new ChildrenNode(ParseTreeType.SET, wildBitNode, true);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted, true);
        testSingleSetValue(inverted, wildBitNode, WildBitMatcher.class, WildBitMatcher.class, 16);
    }

    @Test
    public void testCreateWildbitAnyMatchersFromSet() throws Exception {
        ParseTree wildBitNode = new ChildrenNode(ParseTreeType.ANYBITS, new ByteNode((byte) 0x53), new ByteNode((byte) 0x72));
        ParseTree inverted    = new ChildrenNode(ParseTreeType.ANYBITS, true, new ByteNode((byte) 0x53), new ByteNode((byte) 0x72));
        testSingleSetValue(wildBitNode, inverted, WildBitAnyMatcher.class, WildBitAnyMatcher.class, 240);

        wildBitNode  = new ChildrenNode(ParseTreeType.SET, wildBitNode);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted);
        testSingleSetValue(wildBitNode, inverted, WildBitAnyMatcher.class, WildBitAnyMatcher.class, 240);

        // Put in an inverted nested set - opposite results expected:
        wildBitNode  = new ChildrenNode(ParseTreeType.SET, wildBitNode, true);
        inverted = new ChildrenNode(ParseTreeType.SET, inverted, true);
        testSingleSetValue(inverted, wildBitNode, WildBitAnyMatcher.class, WildBitAnyMatcher.class, 240);
    }


    private void testSingleSetValue(ParseTree child, ParseTree invertedChild,
                                    Class expectedType, Class invertedType,
                                    int numMatches) throws Exception
    {
        ByteMatcherFactory factory = OptimalByteMatcherFactory.FACTORY;
        int inverseMatches = 256 - numMatches;

        // Tests with a non-inverted set to start with:

        // test with nothing inverted.
        ParseTree setNode  = new ChildrenNode(ParseTreeType.SET, child);
        ByteMatcher matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
        assertEquals("Class is not inverted", expectedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", numMatches, matcher.getNumberOfMatchingBytes());

        // test with an inverted byte node
        setNode = new ChildrenNode(ParseTreeType.SET, invertedChild);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
        assertEquals("Class is inverted", invertedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", inverseMatches, matcher.getNumberOfMatchingBytes());

        // test with a byte node and explicitly using non inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, child);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, false, factory);
        assertEquals("Class is not inverted", expectedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", numMatches, matcher.getNumberOfMatchingBytes());

        // test with a byte node and explicitly using the inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, child);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
        assertEquals("Class is inverted", invertedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", inverseMatches, matcher.getNumberOfMatchingBytes());

        // test with an inverted byte node and explicitly using non inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, invertedChild);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, false, factory);
        assertEquals("Class is inverted", invertedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", inverseMatches, matcher.getNumberOfMatchingBytes());

        // test with an inverted byte node and explicitly using the inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, invertedChild);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
        assertEquals("Class is not inverted", expectedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", numMatches, matcher.getNumberOfMatchingBytes());

        // Now test when the containing set itself is already inverted:

        // test with nothing else inverted.
        setNode  = new ChildrenNode(ParseTreeType.SET, child, true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
        assertEquals("Class is inverted", invertedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", inverseMatches, matcher.getNumberOfMatchingBytes());

        // test with an inverted byte node
        setNode = new ChildrenNode(ParseTreeType.SET, invertedChild, true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
        assertEquals("Class is not inverted", expectedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", numMatches, matcher.getNumberOfMatchingBytes());

        // test with a byte node and explicitly using non inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, child, true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, false, factory);
        assertEquals("Class is inverted", invertedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", inverseMatches, matcher.getNumberOfMatchingBytes());

        // test with a byte node and explicitly using the inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, child, true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
        assertEquals("Class is not inverted", expectedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", numMatches, matcher.getNumberOfMatchingBytes());

        // test with an inverted byte node and explicitly using non inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, invertedChild, true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, false, factory);
        assertEquals("Class is not inverted", expectedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", numMatches, matcher.getNumberOfMatchingBytes());

        // test with an inverted byte node and explicitly using the inverted method call
        setNode = new ChildrenNode(ParseTreeType.SET, invertedChild, true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
        assertEquals("Class is inverted ", invertedType, matcher.getClass());
        assertEquals("Matches correct number of bytes", inverseMatches, matcher.getNumberOfMatchingBytes());
    }

    @Test
    public void testCreateAnyMatchersFromSet() throws Exception {
        ByteMatcherFactory factory = OptimalByteMatcherFactory.FACTORY;

        ParseTree setNode = new ChildrenNode(ParseTreeType.SET, new BaseNode(ParseTreeType.ANY));
        ByteMatcher matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
        assertEquals("Class is an any matcher, not a set", AnyByteMatcher.class, matcher.getClass());

        setNode = new ChildrenNode(ParseTreeType.SET, new BaseNode(ParseTreeType.ANY), true);
        matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
        assertEquals("Class is an any matcher, not a set", AnyByteMatcher.class, matcher.getClass());

        try {
            setNode = new ChildrenNode(ParseTreeType.SET, new BaseNode(ParseTreeType.ANY), true);
            matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
            fail("Expected a parse exception trying to create an inverted set from an ANY matcher.");
        } catch (ParseException expectedIgnore) {}

        try {
            setNode = new ChildrenNode(ParseTreeType.SET, new BaseNode(ParseTreeType.ANY), true);
            matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, false, factory);
            fail("Expected a parse exception trying to create an inverted set from an ANY matcher.");
        } catch (ParseException expectedIgnore) {}
    }


    @Test
    public void testSetWithMultipleValues() throws Exception {
        ByteMatcherFactory factory = OptimalByteMatcherFactory.FACTORY;

        List<ParseTree> children = new ArrayList<ParseTree>();
        children.add(new ByteNode((byte) 0));

        for (int i = 2; i < 256; i++) {
            children.add(new ByteNode((byte) i));
            ParseTree setNode = new ChildrenNode(ParseTreeType.SET, children);

            ByteMatcher matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
            assertEquals("Matches correct number of bytes " + i, i, matcher.getNumberOfMatchingBytes());

            setNode = new ChildrenNode(ParseTreeType.SET, children, true);
            matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, factory);
            assertEquals("Matches correct number of bytes " + (256 - i), 256 - i, matcher.getNumberOfMatchingBytes());

            setNode = new ChildrenNode(ParseTreeType.SET, children);
            matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
            assertEquals("Matches correct number of bytes " + (256 - i), 256 - i, matcher.getNumberOfMatchingBytes());

            setNode = new ChildrenNode(ParseTreeType.SET, children, true);
            matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, false, factory);
            assertEquals("Matches correct number of bytes " + (256 - i), 256 - i, matcher.getNumberOfMatchingBytes());

            setNode = new ChildrenNode(ParseTreeType.SET, children, true);
            matcher = MatcherCompilerUtils.createMatcherFromSet(setNode, true, factory);
            assertEquals("Matches correct number of bytes " + i, i, matcher.getNumberOfMatchingBytes());

        }

    }

    @Test
    public void testCreateCaseInsensitiveMatcherString() throws Exception {
        testString("0");
        testString("0abC;rTyH");
        testString("0123456789");
        testString("0123456789abcdefghijklmnopqrstuvwxyz");
        testString("0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    private void testString(String string) throws Exception {
        SequenceMatcher matcher = MatcherCompilerUtils.createCaseInsensitiveMatcher(string);
        assertEquals("Length of matcher and string are the same", string.length(), matcher.length());
        for (int i = 0; i < string.length(); i++) {
            ByteMatcher m = matcher.getMatcherForPosition(i);
            char c = string.charAt(i);
            testMatcher(m, c);
        }
    }

    private void testMatcher(ByteMatcher matcher, char c) {
        if (c >= 'a' && c <= 'z') {
            assertEquals("matches two bytes", 2, matcher.getNumberOfMatchingBytes());
            byte[] matches = matcher.getMatchingBytes();
            assertTrue("matches " + c, matches[0] == (byte) c || matches[1] == (byte) c);
            assertTrue("matches " + c, matches[0] == (byte) (c - 32) || matches[1] == (byte) (c -32));
        } else if (c >= 'A' && c <= 'Z') {
            assertEquals("matches two bytes", 2, matcher.getNumberOfMatchingBytes());
            byte[] matches = matcher.getMatchingBytes();
            assertTrue("matches " + c, matches[0] == (byte) c || matches[1] == (byte) c);
            assertTrue("matches " + c, matches[0] == (byte) (c + 32) || matches[1] == (byte) (c +32));
        } else {
            assertEquals("matches one byte", 1, matcher.getNumberOfMatchingBytes());
            assertEquals("matches correct byte", (byte) c, matcher.getMatchingBytes()[0]);
        }

    }
}

