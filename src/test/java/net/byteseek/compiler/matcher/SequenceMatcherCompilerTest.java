/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.compiler.matcher;

import net.byteseek.parser.Parser;
import net.byteseek.parser.regex.RegexParser;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.utils.ByteUtils;
import net.byteseek.compiler.CompileException;
import net.byteseek.matcher.bytes.*;
import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.FixedGapMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.matcher.sequence.SequenceSequenceMatcher;
import net.byteseek.parser.tree.ParseTree;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class SequenceMatcherCompilerTest {

	static Random random = new Random();

	private SequenceMatcherCompiler	compiler;

	/**
	 * 
	 */
	public SequenceMatcherCompilerTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		final long seed = System.currentTimeMillis();
		// final long seed = ?
		random.setSeed(seed);
		System.out.println("Seeding random number generator with: " + Long.toString(seed));
		System.out.println("To repeat these exact tests, set the seed to the value above.");
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		compiler = new SequenceMatcherCompiler();
	}

	/**
	 * 
	 * @throws CompileException
	 */
	@Test(expected = CompileException.class)
	public void testCompileNullExpression() throws CompileException {
		compiler.compile((String) null);
	}

	/**
	 * 
	 * @throws CompileException
	 */
	@Test(expected = CompileException.class)
	public void testCompileEmptyExpression() throws CompileException {
		compiler.compile("");
	}

	/**
	 * 
	 * @throws CompileException
	 */
	@Test(expected = CompileException.class)
	public void testCompileNullAST() throws CompileException {
		compiler.compile((ParseTree) null);
	}

	/**
	 * Test of compile method, of class SequenceMatcherCompiler.
	 * @throws Exception 
	 */
	@Test
	public void testBasicCompile() throws Exception {
		basicTests("00", 1, OneByteMatcher.class);
		basicTests("01", 1, OneByteMatcher.class);
		basicTests("fF", 1, OneByteMatcher.class);

		basicTests("[00ff]", 1, TwoByteMatcher.class);
		basicTests("[ff00]", 1, TwoByteMatcher.class);
		basicTests("[7f80]", 1, TwoByteMatcher.class);
		basicTests("[807f]", 1, TwoByteMatcher.class);
		basicTests(" [0102]", 1, TwoByteMatcher.class);

		basicTests("'a string'", 8, ByteSequenceMatcher.class);
		basicTests("`a string`", 8, ByteMatcherSequenceMatcher.class);
		basicTests("01 'a string' 02", 10, ByteSequenceMatcher.class);

		basicTests("0102", 2, ByteSequenceMatcher.class);
		basicTests("01 02", 2, ByteSequenceMatcher.class);
		basicTests("01fd", 2, ByteSequenceMatcher.class);

		basicTests(" [0102] &01", 2, ByteMatcherSequenceMatcher.class);
		basicTests(" [0102] [^ffee]", 2, ByteMatcherSequenceMatcher.class);

		basicTests("01fd ef   de", 4, ByteSequenceMatcher.class);

		basicTests("01fd [ef]   de", 4, ByteSequenceMatcher.class);

		basicTests("01fd [ef fe]   de", 4, ByteMatcherSequenceMatcher.class);

		basicTests("01{4}", 4, ByteSequenceMatcher.class);

		basicTests("010203{6}", 8, ByteSequenceMatcher.class);

		basicTests("[fffe]", 1, TwoByteMatcher.class);
		basicTests("[fffe]{5}", 5, ByteMatcherSequenceMatcher.class);

		basicTests("(0102){2}", 4, ByteSequenceMatcher.class);
		basicTests("(dd[ff03]){3}", 6, ByteMatcherSequenceMatcher.class);
		basicTests("'start'(dd[ff03]){3}", 11, SequenceSequenceMatcher.class);

		basicTests(".{1000}", 1000, FixedGapMatcher.class);
	}

	@Test(expected = CompileException.class)
	public void testBadSequenceType() throws CompileException {
		ParseTree badChild = new ChildrenNode(ParseTreeType.ZERO_TO_MANY, new ByteNode((byte) 0));
		ParseTree sequence = new ChildrenNode(ParseTreeType.SEQUENCE, badChild);
		compiler.compile(sequence);
	}

	@Test
	public void testMultiExpressions() throws Exception {
		List<String> expressions = new ArrayList<String>();
		List<SequenceMatcher> generated = new ArrayList<SequenceMatcher>();
		int totalLength = 0;
		for (int i = 0; i < 10; i++) {
			SequenceMatcher gen = createRandomSequenceMatcher();
			totalLength += gen.length();
			expressions.add(gen.toRegularExpression(false));
			generated.add(gen);
		}
		SequenceMatcher joined = compiler.compile(expressions);
		assertEquals("Length of joined expressions is correct", totalLength, joined.length());
	}

	@Test
	public void testJoinExpressions() throws Exception {
		Parser<ParseTree> parser = new RegexParser();
		List<ParseTree> expressions = new ArrayList<ParseTree>();
		List<SequenceMatcher> generated = new ArrayList<SequenceMatcher>();
		for (int i = 0; i < 10; i++) {
			SequenceMatcher gen = createRandomSequenceMatcher();
			ParseTree parsed = parser.parse(gen.toRegularExpression(false));
			expressions.add(parsed);
			generated.add(gen);
		}
		ParseTree joined = compiler.joinExpressions(expressions);
		assertEquals("Parse tree is children class", ChildrenNode.class, joined.getClass());
		assertEquals("Parse tree has 10 children", 10, joined.getNumChildren());
		//TODO: test that the child sequences are equivalent...
	}

	@Test
	public void testRandomSequences() throws CompileException {
		for (int testNo = 0; testNo < 1000; testNo++) {
			SequenceMatcher generated = createRandomSequenceMatcher();
			String expression         = generated.toRegularExpression(false);
			SequenceMatcher compiled = compiler.compile(expression);
			testSequencesEquivalent(expression, generated, compiled);

			expression = generated.toRegularExpression(true);
			compiled   = compiler.compile(expression);
			testSequencesEquivalent(expression, generated, compiled);

			compiled = SequenceMatcherCompiler.compileFrom(expression);
			testSequencesEquivalent(expression, generated, compiled);

			testCompiler(expression, generated, new SequenceMatcherCompiler(new RegexParser()));
			testCompiler(expression, generated, new SequenceMatcherCompiler(new OptimalByteMatcherFactory()));
			testCompiler(expression, generated, new SequenceMatcherCompiler(new SequenceMatcherOptimiser()));
			testCompiler(expression, generated, new SequenceMatcherCompiler(new RegexParser(), new OptimalByteMatcherFactory()));
			testCompiler(expression, generated, new SequenceMatcherCompiler(new RegexParser(), new SequenceMatcherOptimiser()));
			testCompiler(expression, generated, new SequenceMatcherCompiler(new OptimalByteMatcherFactory(), new SequenceMatcherOptimiser()));
		}
	}

	private void testCompiler(String expression, SequenceMatcher generated, SequenceMatcherCompiler comp) throws CompileException {
		SequenceMatcher compiled = comp.compile(expression);
		testSequencesEquivalent(expression, generated, compiled);
	}

	private void testSequencesEquivalent(String expression, SequenceMatcher generated, SequenceMatcher compiled) {
		assertEquals("Generated and compiled are same length: " + expression, generated.length(), compiled.length());
		for (int i = 0; i < generated.length(); i++) {
			ByteMatcher genMatcher = generated.getMatcherForPosition(i);
			ByteMatcher comMatcher = compiled.getMatcherForPosition(i);
			String description = "Matcher at position " + i + " in expression: " + expression + " matches same number of bytes";
			assertEquals(description, genMatcher.getNumberOfMatchingBytes(), comMatcher.getNumberOfMatchingBytes());

			byte[] genBytes = genMatcher.getMatchingBytes();
			Set<Byte> comBytes = ByteUtils.toSet(comMatcher.getMatchingBytes());
			assertEquals("byte arrays are same length", genBytes.length, comBytes.size());
			for (int j = 0; j < genBytes.length; j++) {
				byte genByte = genBytes[j];
				assertTrue("Byte " + genByte + " is in compiled version", comBytes.contains(genByte));
			}
		}
	}

	private SequenceMatcher basicTests(String expression, int length, Class<?> matcherClass) {
		SequenceMatcher matcher = null;
		try {
			matcher = compiler.compile(expression);
			assertEquals("length of " + expression, length, matcher.length());
			assertEquals("class of " + expression, matcherClass, matcher.getClass());
		} catch (CompileException ex) {
			fail(expression + " " + ex.getMessage());
		}
		return matcher;
	}


	private SequenceMatcher createRandomSequenceMatcher() {
		List<SequenceMatcher> matchers = new ArrayList<SequenceMatcher>();
		int elements = random.nextInt(15) + 1;
		for (int i = 0; i < elements; i++) {
			matchers.add(randomSequenceMatcher());
		}
		return new SequenceSequenceMatcher(matchers);
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
				return new InvertedByteMatcher((byte) random.nextInt(256));
			case 3:
				return new ByteRangeMatcher(random.nextInt(256), random.nextInt(256), inverted);
			case 4:
				return new SetBinarySearchMatcher(createRandomByteSet(), inverted);
			case 5:
				return new SetBitsetMatcher(createRandomByteSet(), inverted);
			case 6:
				return new TwoByteMatcher((byte) random.nextInt(256), (byte) random.nextInt(256));
			case 7:
				return new AllBitmaskMatcher((byte) random.nextInt(256), inverted);
			case 8:
				return new AnyBitmaskMatcher((byte) random.nextInt(256), inverted);
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

	private SequenceMatcher randomSequenceMatcher() {
		int matcherType = random.nextInt(4);
		switch (matcherType) {
			case 0: return createRandomByteMatcher();
			case 1: return new FixedGapMatcher(random.nextInt(10) + 1);
			case 2: return createRandomByteArrayMatcher();
			case 3: return createRandomByteMatcherSequence();
			default: throw new RuntimeException("No such type of matcher defined.");
		}
	}

	private ByteSequenceMatcher createRandomByteArrayMatcher() {
		int length = random.nextInt(15) + 1;
		byte[] array = new byte[length];
		for (int i = 0; i < length; i++) {
			array[i] = (byte) random.nextInt(256);
		}
		return new ByteSequenceMatcher(array);
	}

	private ByteMatcherSequenceMatcher createRandomByteMatcherSequence() {
		int length = random.nextInt(15) + 1;
		ByteMatcher[] array = new ByteMatcher[length];
		for (int i = 0; i < length; i++) {
			array[i] = createRandomByteMatcher();
		}
		return new ByteMatcherSequenceMatcher(array);
	}

}