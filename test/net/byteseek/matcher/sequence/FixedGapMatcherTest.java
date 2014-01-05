package net.byteseek.matcher.sequence;

import static org.junit.Assert.*;

import net.byteseek.matcher.bytes.ByteMatcher;

import org.junit.Test;

public class FixedGapMatcherTest {

	
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
		assertEquals("Matcher of length three is ..", "...", matcher.toRegularExpression(true));
		assertEquals("Matcher of length three is ..", "...", matcher.toRegularExpression(false));

		matcher = new FixedGapMatcher(4);
		assertEquals("Matcher of length four is .{4}", ".{4}", matcher.toRegularExpression(true));
		assertEquals("Matcher of length four is .{4}", ".{4}", matcher.toRegularExpression(false));

		matcher = new FixedGapMatcher(19);
		assertEquals("Matcher of length nineteen is .{19}", ".{19}", matcher.toRegularExpression(true));
		assertEquals("Matcher of length nineteen is .{19}", ".{19}", matcher.toRegularExpression(false));
	}

	
	@Test
	public final void testMatchesWindowReaderLong() {
		fail("Not yet implemented"); // TODO
	}

	
	@Test
	public final void testMatchesByteArrayInt() {
		fail("Not yet implemented"); // TODO
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
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testSubsequenceInt() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testRepeat() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testToString() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testIterator() {
		fail("Not yet implemented"); // TODO
	}

}
