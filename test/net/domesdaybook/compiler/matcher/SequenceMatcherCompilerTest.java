/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.compiler.matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.domesdaybook.compiler.CompileException;
import net.domesdaybook.matcher.bytes.AllBitmaskMatcher;
import net.domesdaybook.matcher.bytes.ByteRangeMatcher;
import net.domesdaybook.matcher.bytes.OneByteMatcher;
import net.domesdaybook.matcher.bytes.SetBinarySearchMatcher;
import net.domesdaybook.matcher.sequence.ByteArrayMatcher;
import net.domesdaybook.matcher.sequence.ByteMatcherArrayMatcher;
import net.domesdaybook.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.domesdaybook.matcher.sequence.FixedGapMatcher;
import net.domesdaybook.matcher.sequence.SequenceArrayMatcher;
import net.domesdaybook.matcher.sequence.SequenceMatcher;
import net.domesdaybook.parser.tree.ParseTree;

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

		basicTests("[00ff]", 1, SetBinarySearchMatcher.class);
		basicTests("[ff00]", 1, SetBinarySearchMatcher.class);
		basicTests("[7f80]", 1, ByteRangeMatcher.class);
		basicTests("[807f]", 1, ByteRangeMatcher.class);
		basicTests(" [0102]", 1, ByteRangeMatcher.class);

		basicTests("'a string'", 8, ByteArrayMatcher.class);
		basicTests("`a string`", 8, CaseInsensitiveSequenceMatcher.class);
		basicTests("01 'a string' 02", 10, ByteArrayMatcher.class);

		basicTests("0102", 2, ByteArrayMatcher.class);
		basicTests("01 02", 2, ByteArrayMatcher.class);
		basicTests("01fd", 2, ByteArrayMatcher.class);

		basicTests(" [0102] &01", 2, ByteMatcherArrayMatcher.class);
		basicTests(" [0102] [^ffee]", 2, ByteMatcherArrayMatcher.class);

		basicTests("01fd ef   de", 4, ByteArrayMatcher.class);

		basicTests("01fd [ef]   de", 4, ByteArrayMatcher.class);

		basicTests("01fd [ef fe]   de", 4, SequenceArrayMatcher.class);

		basicTests("01{4}", 4, ByteArrayMatcher.class);

		basicTests("010203{6}", 8, ByteArrayMatcher.class);

		basicTests("[fffe]", 1, AllBitmaskMatcher.class);
		basicTests("[fffe]{5}", 5, ByteMatcherArrayMatcher.class);

		basicTests("(0102){2}", 4, ByteArrayMatcher.class);
		basicTests("(dd[ff03]){3}", 6, SequenceArrayMatcher.class);
		basicTests("'start'(dd[ff03]){3}", 11, SequenceArrayMatcher.class);

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