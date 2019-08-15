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

import net.byteseek.compiler.CompileException;
import net.byteseek.incubator.matcher.bytes.SetBinarySearchMatcher;
import net.byteseek.parser.regex.RegexParser;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.utils.ByteUtils;
import net.byteseek.matcher.bytes.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static net.byteseek.utils.ByteUtils.toList;
import static org.junit.Assert.*;

public class ByteMatcherCompilerTest {

    static Random random = new Random();

    private ByteMatcherCompiler comp;

    @BeforeClass
    public static void setUpClass() throws Exception {
        final long seed = System.currentTimeMillis();
        // final long seed = 1561725862862L; - causes issue with translation from inverted range to wildbitany using compileInvertedFrom.
        random.setSeed(seed);
        System.out.println("Seeding random number generator with: " + Long.toString(seed));
        System.out.println("To repeat these exact tests, set the seed to the value above.");
    }

    @Before
    public void setup() {
        comp = new ByteMatcherCompiler();
    }

    @Test(expected = CompileException.class)
    public void testCompileNullExpression() throws CompileException {
        comp.compile((String) null);
    }

    @Test(expected = CompileException.class)
    public void testCompileEmptyExpression() throws CompileException {
        comp.compile("");
    }

    @Test(expected = CompileException.class)
    public void testCompileNullAST() throws CompileException {
        comp.compile((ParseTree) null);
    }

    @Test(expected = CompileException.class)
    public void testBadSequenceType() throws CompileException {
        ParseTree badChild = new ChildrenNode(ParseTreeType.ZERO_TO_MANY, new ByteNode((byte) 0));
        ParseTree sequence = new ChildrenNode(ParseTreeType.SEQUENCE, badChild);
        comp.compile(sequence);
    }

    @Test
    public void testCompileInvertedFrom() throws Exception {
        for (int testNo = 0; testNo < 1000; testNo++) {
            ByteMatcher generated = createRandomByteMatcher();
            final byte[] genBytes = generated.getMatchingBytes();

            // Occasionally, the random expression generator will create an expression that matches all bytes.
            if (genBytes.length < 256) {
                ByteMatcher compiled = ByteMatcherCompiler.compileInvertedFrom(genBytes);
                byte[] comBytes = compiled.getMatchingBytes();
                assertEquals("Matcher " + generated + " matches same as " + compiled, 256 - genBytes.length, comBytes.length);
                Set<Byte> comB = ByteUtils.toSet(comBytes);
                for (byte b : genBytes) {
                    assertFalse(comB.contains(b));
                }
            }
        }
    }

    @Test
    public void testCompile() throws Exception {
        testCompile(new ByteMatcherCompiler());
        testCompile(new ByteMatcherCompiler(new OptimalByteMatcherFactory()));
        testCompile(new ByteMatcherCompiler(new RegexParser()));
    }

    private void testCompile(final ByteMatcherCompiler compiler) throws Exception {
        for (int testNo = 0; testNo < 1000; testNo++) {
            ByteMatcher generated = createRandomByteMatcher();
            String expression = generated.toRegularExpression(false);

            ByteMatcher compiled = compiler.compile(expression);

            byte[] genBytes = generated.getMatchingBytes();
            byte[] comBytes = compiled.getMatchingBytes();
            if (genBytes.length != comBytes.length) {
                System.out.println("Matcher " + generated + " not the same as " + compiled); //TODO: get rid of debug tests.
            }
            assertEquals("Matcher " + generated + " matches same as " + compiled, genBytes.length, comBytes.length);
            Set<Byte> comB = ByteUtils.toSet(comBytes);
            for (byte b : genBytes) {
                assertTrue(expression + " " + generated.toString() + " " + compiled.toString(), comB.contains(b));
            }
        }
    }

    @Test
    public void testCompileFrom() throws Exception {
        for (int testNo = 0; testNo < 1000; testNo++) {
            ByteMatcher generated = createRandomByteMatcher();
            String expression = generated.toRegularExpression(false);
            final byte[] genBytes = generated.getMatchingBytes();

            // Occasionally, the random expression generator will create an expression that matches no bytes.
            if (genBytes.length > 0) {


                ByteMatcher compiled = ByteMatcherCompiler.compileFrom(expression);
                byte[] comBytes = compiled.getMatchingBytes();
                assertEquals("Matcher " + generated + " matches same as " + compiled, genBytes.length, comBytes.length);
                Set<Byte> comB = ByteUtils.toSet(comBytes);
                for (byte b : genBytes) {
                    assertTrue(comB.contains(b));
                }

                compiled = ByteMatcherCompiler.compileFrom(genBytes);
                comBytes = compiled.getMatchingBytes();
                assertEquals("Matcher " + generated + " matches same as " + compiled, genBytes.length, comBytes.length);
                comB.clear();
                comB = ByteUtils.toSet(comBytes);
                for (byte b : genBytes) {
                    assertTrue(comB.contains(b));
                }
            }
        }
    }


    @Test
    public void testCompileCollection() throws Exception {
        for (int testNo = 0; testNo < 1000; testNo++) {
            Set<Byte> allBytesMatching = new HashSet<Byte>();
            Collection<String> expressions = new ArrayList<String>();
            int numExpressions = random.nextInt(10) + 1;
            for (int i = 0; i < numExpressions; i++) {
                ByteMatcher generated = createRandomByteMatcher();
                expressions.add(generated.toRegularExpression(false));
                byte[] genBytes = generated.getMatchingBytes();
                allBytesMatching.addAll(toList(genBytes));
            }

            ByteMatcher compiled = comp.compile(expressions);
            byte[] comBytes = compiled.getMatchingBytes();

            assertEquals("Matcher matches same number of bytes", allBytesMatching.size(), comBytes.length);
            Set<Byte> comB = ByteUtils.toSet(comBytes);
            for (byte b : comBytes) {
                assertTrue(allBytesMatching.contains(b));
            }
        }
    }

    @Test(expected=CompileException.class)
    public void testJoinNullExpressions() throws Exception {
        comp.joinExpressions(null);
    }

    @Test(expected=CompileException.class)
    public void testJoinEmptyExpressions() throws Exception {
        comp.joinExpressions(new ArrayList<ParseTree>());
    }

    @Test
    public void testJoinSingleExpressionJustReturnsChild() throws Exception {
        ParseTree singleChild = new IntNode(10);
        List<ParseTree> list = new ArrayList<ParseTree>();
        list.add(singleChild);
        ParseTree tree = comp.joinExpressions(list);
        assertEquals(singleChild, tree);
    }

    @Test
    public void testJoinMultiExpressions() throws Exception {
        List<ParseTree> expressions = new ArrayList<ParseTree>();
        expressions.add(new IntNode(0));
        for (int numChildren = 2; numChildren < 10; numChildren++) {
            expressions.add(new IntNode(numChildren -1));
            ParseTree result = comp.joinExpressions(expressions);
            assertEquals(ParseTreeType.SET, result.getParseTreeType());
            assertEquals(numChildren, result.getNumChildren());
            for (int childIndex = 0; childIndex < numChildren; childIndex++) {
                ParseTree child = result.getChild(childIndex);
                assertEquals(ParseTreeType.INTEGER, child.getParseTreeType());
                assertEquals(childIndex, child.getIntValue());
            }
        }
    }

    private ByteMatcher createRandomByteMatcher() {
        int matcherType = random.nextInt(9);
        boolean inverted = random.nextBoolean();
        switch (matcherType) {
            case 0:
                return AnyByteMatcher.ANY_BYTE_MATCHER;
            case 1:
                return OneByteMatcher.valueOf((byte) random.nextInt(256));
            case 2:
                return new OneByteInvertedMatcher((byte) random.nextInt(256));
            case 3:
                return new ByteRangeMatcher(random.nextInt(256), random.nextInt(256), inverted);
            case 4:
                return new SetBinarySearchMatcher(createRandomByteSet(), inverted);
            case 5:
                return new SetBitmapMatcher(createRandomByteSet(), inverted);
            case 6:
                return new TwoByteMatcher((byte) random.nextInt(256), (byte) random.nextInt(256));
            case 7:
                return new WildBitMatcher((byte) random.nextInt(256), (byte) random.nextInt(256), inverted);
            case 8:
                return new WildBitAnyMatcher((byte) random.nextInt(256), (byte) random.nextInt(256), inverted);
            default:
                throw new RuntimeException("Case statement doesn't support value " + matcherType);
        }
    }

    private Set<Byte> createRandomByteSet() {
        Set<Byte> bytes = new HashSet<Byte>();
        int numElements = random.nextInt(255) + 1;
        for (int i = 0; i < numElements; i++) {
            byte value = (byte) random.nextInt(256);
            bytes.add(value);
        }
        return bytes;
    }
}