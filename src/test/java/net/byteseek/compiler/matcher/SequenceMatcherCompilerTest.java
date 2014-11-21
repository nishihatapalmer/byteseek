/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.compiler.matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import net.byteseek.compiler.CompileException;
import net.byteseek.matcher.bytes.AllBitmaskMatcher;
import net.byteseek.matcher.bytes.ByteRangeMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.matcher.bytes.SetBinarySearchMatcher;
import net.byteseek.matcher.bytes.TwoByteMatcher;
import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.FixedGapMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.matcher.sequence.SequenceSequenceMatcher;
import net.byteseek.parser.tree.ParseTree;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author matt
 */
public class SequenceMatcherCompilerTest {

	private SequenceMatcherCompiler	compiler	= new SequenceMatcherCompiler();

	/**
	 * 
	 */
	public SequenceMatcherCompilerTest() {
	}

	/**
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	/**
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownClass() throws Exception {
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

		basicTests("01fd [ef fe]   de", 4, SequenceSequenceMatcher.class);

		basicTests("01{4}", 4, ByteSequenceMatcher.class);

		basicTests("010203{6}", 8, ByteSequenceMatcher.class);

		basicTests("[fffe]", 1, AllBitmaskMatcher.class);
		basicTests("[fffe]{5}", 5, ByteMatcherSequenceMatcher.class);

		basicTests("(0102){2}", 4, ByteSequenceMatcher.class);
		basicTests("(dd[ff03]){3}", 6, SequenceSequenceMatcher.class);
		basicTests("'start'(dd[ff03]){3}", 11, SequenceSequenceMatcher.class);

		basicTests(".{1000}", 1000, FixedGapMatcher.class);
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

}