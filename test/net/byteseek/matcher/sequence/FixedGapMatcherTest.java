/*
 * Copyright Matt Palmer 2014, All rights reserved.
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

package net.byteseek.matcher.sequence;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.byteseek.io.reader.FileReader;
import net.byteseek.matcher.bytes.ByteMatcher;

import org.junit.Before;
import org.junit.Test;

public class FixedGapMatcherTest {

	private FileReader reader;
	private byte[] bytes;
	
	/**
	 * Creates a file reader and a byte array from an ASCII test file.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		reader = new FileReader(getFile("/TestASCII.txt"));
		bytes = reader.getWindow(0).getArray();
	}
	
	@Test
	public final void testFixedGapMatcher() {
		for (int i = 1; i < 20; i++) {
			FixedGapMatcher matcher = new FixedGapMatcher(i);
			assertEquals("Length is correct " + i, i, matcher.length());
			for (ByteMatcher bm : matcher) {
				assertEquals("Matches 256 bytes", 256, bm.getNumberOfMatchingBytes());
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testZeroGapConstructor() {
		new FixedGapMatcher(0);
	}
	
	
	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testNegativeGapConstructor() {
		new FixedGapMatcher(-1);
	}

	
	@Test
	public final void testGetMatcherForPosition() {
		FixedGapMatcher matcher = new FixedGapMatcher(20);
		for (int i = 0; i < 20; i++) {
			ByteMatcher bm = matcher.getMatcherForPosition(i);
			assertEquals("Matches 256 bytes", 256, bm.getNumberOfMatchingBytes());
		}
	}

	
	@Test
	public final void testLength() {
		for (int i = 1; i < 20; i++) {
			FixedGapMatcher matcher = new FixedGapMatcher(i);
			assertEquals("Length is correct " + i, i, matcher.length());
		}
	}

	
	@Test
	public final void testToRegularExpression() {
		FixedGapMatcher matcher = new FixedGapMatcher(1);
		assertEquals("Matcher of length one is .", ".", matcher.toRegularExpression(true));
		assertEquals("Matcher of length one is .", ".", matcher.toRegularExpression(false));
		
		matcher = new FixedGapMatcher(2);
		assertEquals("Matcher of length two is ..", "..", matcher.toRegularExpression(true));
		assertEquals("Matcher of length two is ..", "..", matcher.toRegularExpression(false));

		matcher = new FixedGapMatcher(3);
		assertEquals("Matcher of length three is ...", "...", matcher.toRegularExpression(true));
		assertEquals("Matcher of length three is ...", "...", matcher.toRegularExpression(false));

		matcher = new FixedGapMatcher(4);
		assertEquals("Matcher of length four is .{4}", ".{4}", matcher.toRegularExpression(true));
		assertEquals("Matcher of length four is .{4}", ".{4}", matcher.toRegularExpression(false));

		matcher = new FixedGapMatcher(19);
		assertEquals("Matcher of length nineteen is .{19}", ".{19}", matcher.toRegularExpression(true));
		assertEquals("Matcher of length nineteen is .{19}", ".{19}", matcher.toRegularExpression(false));
	}

	
	// /////////////////////////////
	// reader matching tests //
	// /////////////////////////////

	@Test
	public void testMatches_ByteReader_long() throws FileNotFoundException, IOException {
		SequenceMatcher matcher = new FixedGapMatcher(3);
		runTestMatchesAround(matcher, 1, 61, 1017);

		matcher = new FixedGapMatcher(3).reverse();
		runTestMatchesAround(matcher, 1, 61, 1017);

		matcher = new FixedGapMatcher(4);
		runTestMatchesAround(matcher, 28200, 60836, 64481);

		matcher = matcher.subsequence(1, 4);
		runTestMatchesAround(matcher, 28201, 60837, 64482);

		matcher = new FixedGapMatcher(4).reverse();
		runTestMatchesAround(matcher, 28200, 60836, 64481);

		matcher = matcher.subsequence(1, 3);
		runTestMatchesAround(matcher, 28201, 60837, 64482);

		matcher = new FixedGapMatcher(3);
		runTestMatchesAround(matcher, 196, 42004, 112276);

		matcher = matcher.subsequence(1);
		runTestMatchesAround(matcher, 197, 42005, 112277);

		matcher = new FixedGapMatcher(3).reverse();
		runTestMatchesAround(matcher, 196, 42004, 112276);

		matcher = matcher.subsequence(1);
		runTestMatchesAround(matcher, 197, 42005, 112277);
	}

	/**
	 * Test matching successfully over a window boundary. A FileReader uses a
	 * default window size of 4096, so the last position in the first window is
	 * 4095.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testMatchesOverBoundary_ByteReader_long() throws FileNotFoundException, IOException {
		// Test around a window boundary at 4096
		SequenceMatcher matcher = new FixedGapMatcher(2);
		runTestMatchesAround(matcher, 4095);

		matcher = new FixedGapMatcher(2).reverse();
		runTestMatchesAround(matcher, 4095);

		matcher = new FixedGapMatcher(9);
		runTestMatchesAround(matcher, 4090);

		matcher = matcher.reverse();
		runTestMatchesAround(matcher, 4090);
	}

	// //////////////////////////////////
	// reader out of bounds tests //
	// //////////////////////////////////

	@Test
	public void testMatchesReaderOutOfBoundsNegative() throws IOException {
		SequenceMatcher matcher = new FixedGapMatcher(3);
		assertFalse("negative position", matcher.matches(reader, -1));
		assertFalse("past end", matcher.matches(reader, 10000000));

		matcher = matcher.reverse();
		assertFalse("reverse negative position", matcher.matches(reader, -1));
		assertFalse("reverse past end", matcher.matches(reader, 10000000));
	}

	@Test
	public void testMatchesReaderOutOfBoundsCrossingEnd() throws IOException {
		SequenceMatcher matcher = new FixedGapMatcher(5);
		assertFalse("longer than end", matcher.matches(reader, 112276));

		matcher = new FixedGapMatcher(5).reverse();
		assertFalse("reverse longer than end", matcher.matches(reader, 112276));
	}

	// ///////////////////////////////
	// byte array matches tests //
	// ///////////////////////////////

	@Test
	public void testMatches_byteArr_int() {
		SequenceMatcher matcher = new FixedGapMatcher(3);
		runTestMatchesAroundArray(matcher, 1, 61, 1017);

		matcher = matcher.reverse();
		runTestMatchesAroundArray(matcher, 1, 61, 1017);
	}

	@Test
	public void testMatchesNoBoundsCheck_byteArr_int() {
		SequenceMatcher matcher = new FixedGapMatcher(3);
		testMatchesAroundArrayNoCheck(matcher, 61);
		testMatchesAroundArrayNoCheck(matcher, 1017);

		matcher = matcher.reverse();
		testMatchesAroundArrayNoCheck(matcher, 61);
		testMatchesAroundArrayNoCheck(matcher, 1017);
	}

	// //////////////////////////////////////
	// byte array out of bounds tests //
	// //////////////////////////////////////

	@Test
	public void testMatches_outOfBoundsNegative() {
		SequenceMatcher matcher = new FixedGapMatcher(7);
		assertFalse("matches at negative pos", matcher.matches(bytes, -1));

		matcher = matcher.reverse();
		assertFalse("matches at negative pos", matcher.matches(bytes, -1));
	}

	@Test // FixedGapMatcher does not throw exception if crossing array boundary. (expected = ArrayIndexOutOfBoundsException.class)
	public void testMatchesNoBoundsCheck_outOfBoundsNegative() {
		SequenceMatcher matcher = new FixedGapMatcher(6);
		matcher.matchesNoBoundsCheck(bytes, -1);

		matcher = matcher.reverse();
		matcher.matchesNoBoundsCheck(bytes, -1);
	}

	@Test
	public void testMatches_outOfBoundsPastEnd() {
		SequenceMatcher matcher = new FixedGapMatcher(6);
		assertFalse("matches past end", matcher.matches(bytes, 4096));

		matcher = matcher.reverse();
		assertFalse("matches past end", matcher.matches(bytes, 4096));
	}

	@Test // FixedGapMatcher does not throw exception if crossing array boundary. (expected = ArrayIndexOutOfBoundsException.class)
	public void testMatchesNoBoundsCheck_outOfBoundsPastEnd() {
		SequenceMatcher matcher = new FixedGapMatcher(4);
		matcher.matchesNoBoundsCheck(bytes, 4096);
	}

	@Test // FixedGapMatcher does not throw exception if crossing array boundary. (expected = ArrayIndexOutOfBoundsException.class)
	public void testReverseMatchesNoBoundsCheck_outOfBoundsPastEnd() {
		SequenceMatcher matcher = new FixedGapMatcher(4);
		matcher = matcher.reverse();
		matcher.matchesNoBoundsCheck(bytes, 4096);
	}

	@Test
	public void testMatches_outOfBoundsCrossingEnd() {
		SequenceMatcher matcher = new FixedGapMatcher(2);
		assertFalse("matches crossing end", matcher.matches(bytes, 4095));

		matcher = matcher.reverse();
		assertFalse("matches crossing end", matcher.matches(bytes, 4095));
	}

	@Test // FixedGapMatcher does not throw exception if crossing array boundary. (expected = ArrayIndexOutOfBoundsException.class)
	public void testMatchesNoBoundsCheck_outOfBoundsCrossingEnd() {
		FixedGapMatcher matcher = new FixedGapMatcher(2);
		matcher.matchesNoBoundsCheck(bytes, 4095);
	}

	@Test // FixedGapMatcher does not throw exception if crossing array boundary. (expected = ArrayIndexOutOfBoundsException.class)
	public void testReverseMatchesNoBoundsCheck_outOfBoundsCrossingEnd() {
		SequenceMatcher matcher = new FixedGapMatcher(2);
		matcher = matcher.reverse();
		matcher.matchesNoBoundsCheck(bytes, 4095);
	}
	
	/**
	 * Tests that:
	 * 
	 * - a matcher matches at a given position in a byte array using a no bounds
	 * check match. - it does not match one position behind that position. - it
	 * does not match one position ahead of that position.
	 * 
	 * @param matcher
	 * @param pos
	 */
	private void testMatchesAroundArrayNoCheck(SequenceMatcher matcher, int pos) {
		String matchDesc = matcher.toRegularExpression(true);
		assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matchesNoBoundsCheck(bytes, pos));
		assertTrue(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matchesNoBoundsCheck(bytes, pos - 1));
		assertTrue(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matchesNoBoundsCheck(bytes, pos + 1));
	}

	@Test
	public final void testMatchesNoBoundsCheck() {
		byte[] bytes = new byte[10];
		for (int i = 1; i < 33; i++) {
			FixedGapMatcher matcher = new FixedGapMatcher(i);
			for (int j = -1; j < 12; j++) {
				assertTrue("matcher always matches", matcher.matchesNoBoundsCheck(bytes, j));
				assertTrue("matcher always matches", matcher.matchesNoBoundsCheck(null, j));
			}
		}
	}

	
	@Test
	public final void testReverse() {
		for (int i = 1; i < 33; i++) {
			FixedGapMatcher matcher = new FixedGapMatcher(i);
			FixedGapMatcher reverse = matcher.reverse();
			assertTrue("Reverse is the same as forwards", matcher == reverse);
		}
	}

	
	@Test
	public final void testSubsequenceIntInt() {
		FixedGapMatcher matcher = new FixedGapMatcher(100);
		for (int i = 1; i < 100; i++) {
			SequenceMatcher matcher2 = matcher.subsequence(i, 100);
			assertEquals("Length is " + (100 - i), 100 -i, matcher2.length());
		}

		for (int i = 1; i <= 100; i++) {
			SequenceMatcher matcher2 = matcher.subsequence(0, i);
			assertEquals("Length is " + i, i, matcher2.length());
		}
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public final void testSubsequenceIntIntNegativeStart() {
		new FixedGapMatcher(10).subsequence(-1, 4);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public final void testSubsequenceIntIntPastEnd() {
		new FixedGapMatcher(10).subsequence(10, 10);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public final void testSubsequenceIntIntEndPastLength() {
		new FixedGapMatcher(10).subsequence(1, 11);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public final void testSubsequenceIntIntStartPastEnd() {
		new FixedGapMatcher(10).subsequence(5, 5);
	}
	
	@Test
	public final void testSubsequenceInt() {
		FixedGapMatcher matcher = new FixedGapMatcher(100);
		for (int i = 1; i < 100; i++) {
			SequenceMatcher matcher2 = matcher.subsequence(i);
			assertEquals("Length is " + (100 - i), 100 -i, matcher2.length());
		}
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public final void testSubsequenceIntNegativeStart() {
		new FixedGapMatcher(10).subsequence(-1);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public final void testSubsequenceIntPastEnd() {
		new FixedGapMatcher(10).subsequence(10);
	}
	

	@Test
	public final void testRepeat() {
		SequenceMatcher matcher = new FixedGapMatcher(1);
		int size = 1;
		for (int i = 1; i < 10; i++) {
			assertEquals("matcher equals size " + size, size, matcher.length());
			matcher = matcher.repeat(i);
			size = size * i;
		}
	}
	

	@Test(expected = IllegalArgumentException.class)
	public final void testZeroRepeat() {
		new FixedGapMatcher(1).repeat(0);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public final void testNegativeRepeat() {
		new FixedGapMatcher(1).repeat(-1);
	}
	

	@Test
	public final void testToString() {
		FixedGapMatcher matcher = new FixedGapMatcher(4);
		assertTrue("String contains class name", matcher.toString().contains("FixedGapMatcher"));
		assertTrue("String contains expression", matcher.toString().contains(matcher.toRegularExpression(true)));
	}

	@Test
	public final void testIterator() {
		FixedGapMatcher matcher = new FixedGapMatcher(10);
		Iterator<ByteMatcher> iterator = matcher.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			try {
				iterator.remove();
				fail("Expected an UnsupportedOperationException");
			} catch (UnsupportedOperationException expected) {};

			ByteMatcher match = iterator.next();
			assertEquals("FixedGapMatcher iterator produces a match for 256 bytes", 256, match.getNumberOfMatchingBytes());
			count++;
		}
		assertEquals("Count is 10", 10, count);
		
		try {
			iterator.next();
			fail("Expected a NoSuchElementException");
		} catch (NoSuchElementException expected) {}
	}

	/**
	 * Tests that a sequence matcher matches at a series of positions, but not
	 * immediately surrounding them using a WindowReader interface.
	 * 
	 * Also tests that the reverse of the reverse of the matcher has identical
	 * behaviour.
	 * 
	 * @param matcher
	 * @param positions
	 * @throws IOException
	 */
	private void runTestMatchesAround(SequenceMatcher matcher, long... positions) throws IOException {
		runTestMatchesAroundOriginal(matcher, positions);
		runTestMatchesAroundDoubleReversed(matcher, positions);
	}

	/**
	 * Tests that a sequence matcher matches at a series of positions, but not
	 * immediately surrounding them, using a WindowReader interface.
	 * 
	 * @param matcher
	 * @param positions
	 * @throws IOException
	 */
	private void runTestMatchesAroundOriginal(SequenceMatcher matcher, long... positions) throws IOException {
		for (long position : positions) {
			testMatchesAroundReader(matcher, position);
		}
	}

	/**
	 * Tests that the reverse of the reverse of a sequence matcher matches at a
	 * series of positions, but not immediately surrounding them, using a
	 * WindowReader interface.
	 * 
	 * @param matcher
	 * @param positions
	 * @throws IOException
	 */
	private void runTestMatchesAroundDoubleReversed(SequenceMatcher matcher, long... positions) throws IOException {
		SequenceMatcher doubleReversed = matcher.reverse().reverse();
		for (long position : positions) {
			testMatchesAroundReader(doubleReversed, position);
		}
	}
	
	/**
	 * Tests that:
	 * 
	 * - a matcher matches at a given position in a FileReader. - it does not
	 * match one position behind that position. - it does not match one position
	 * ahead of that position.
	 * 
	 * @param matcher
	 * @param pos
	 * @throws IOException
	 */
	private void testMatchesAroundReader(SequenceMatcher matcher, long pos) throws IOException {
		String matchDesc = matcher.toRegularExpression(true);
		assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(reader, pos));
		assertTrue(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(reader, pos - 1));
		assertTrue(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(reader, pos + 1));
	}
	
	/**
	 * Tests that a sequence matcher matches at a series of positions, but not
	 * immediately surrounding them, using a byte array.
	 * 
	 * Also tests that the reverse of the reverse of the matcher has identical
	 * behaviour.
	 * 
	 * @param matcher
	 * @param positions
	 * @throws IOException
	 */
	private void runTestMatchesAroundArray(SequenceMatcher matcher, int... positions) {
		runTestMatchesAroundOriginalArray(matcher, positions);
		runTestMatchesAroundDoubleReversedArray(matcher, positions);
	}

	/**
	 * Tests that a sequence matcher matches at a series of positions, but not
	 * immediately surrounding them, using a byte array.
	 * 
	 * @param matcher
	 * @param positions
	 * @throws IOException
	 */
	private void runTestMatchesAroundOriginalArray(SequenceMatcher matcher, int... positions) {
		for (int position : positions) {
			testMatchesAroundArray(matcher, position);
		}
	}

	/**
	 * Tests that the reverse of the reverse of a sequence matcher matches at a
	 * series of positions, but not immediately surrounding them, using a byte
	 * array.
	 * 
	 * @param matcher
	 * @param positions
	 * @throws IOException
	 */
	private void runTestMatchesAroundDoubleReversedArray(SequenceMatcher matcher, int... positions) {
		SequenceMatcher doubleReversed = matcher.reverse().reverse();
		for (int position : positions) {
			testMatchesAroundArray(doubleReversed, position);
		}
	}
	
	/**
	 * Tests that:
	 * 
	 * - a matcher matches at a given position in a byte array. - it does not
	 * match one position behind that position. - it does not match one position
	 * ahead of that position.
	 * 
	 * @param matcher
	 * @param pos
	 */
	private void testMatchesAroundArray(SequenceMatcher matcher, int pos) {
		String matchDesc = matcher.toRegularExpression(true);
		assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(bytes, pos));
		assertTrue(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(bytes, pos - 1));
		assertTrue(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(bytes, pos + 1));
	}
	
	/**
	 * Returns a file given a resource name of a file in the test packages.
	 * 
	 * @param resourceName
	 * @return
	 */
	private File getFile(final String resourceName) {
		URL url = this.getClass().getResource(resourceName);
		return new File(url.getPath());
	}
	
}
