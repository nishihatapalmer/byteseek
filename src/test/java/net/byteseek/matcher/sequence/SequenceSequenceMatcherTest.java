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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import net.byteseek.io.reader.FileReader;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.matcher.bytes.TwoByteMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests all the constructors and public methods of the SequenceSequenceMatcher
 * class for both success and failure, and the associated
 * ReverseSequenceSequenceMatcher class for all the same conditions.
 * 
 * In particular, it tests for out-of-bounds, next-to-boundary and
 * boundary-crossing conditions in the matching methods.
 * 
 * @author Matt Palmer
 */
public class SequenceSequenceMatcherTest {

	// ////////////////
	// test setup    //
	// ////////////////

	private final static Random rand = new Random();

//	private FileReader reader;
//	private FileReader reader2;
	List<FileReader> readers;
	private byte[] bytes;

	public SequenceSequenceMatcherTest() {
	}

	/**
	 * Generates a random number to use in randomising tests where complete
	 * coverage takes too long. The seed is output to the console to give a
	 * fighting chance of replicating a failing test - but I'm not really
	 * convinced this is a very pleasant way of testing. Still, it gives more
	 * complete coverage of the code than purely fixed tests.
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		final long seed = System.currentTimeMillis();
		// final long seed = ?
		rand.setSeed(seed);
		System.out.println("Seeding random number generator with: " + Long.toString(seed));
		System.out.println("To repeat these exact tests, set the seed to the value above.");
	}

	/**
	 * Creates a file reader and a byte array from an ASCII test file.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		readers = getReaders("/TestASCII.txt", 1, 10, 4092, 4100);
		//reader = new FileReader(getFile("/TestASCII.txt"));
		//reader2 = new FileReader(getFile("/TestASCII.txt"), 3);
		bytes = readers.get(14).getWindow(0).getArray();
	}
	
	@After
	public void tearDown() throws Exception {
		for (FileReader reader : readers) {
			reader.close();
		}
		readers.clear();
	}

	// /////////////////////////
	// constructor tests //
	// /////////////////////////

	/**
	 * Construct using random arrays of ByteMatchers, 100 times. Tests are:
	 * 
	 * - the length is correct. - each position in the matcher only matches one
	 * byte. - each byte in the matcher is correct. - that the array passed in
	 * was defensively copied.
	 */
	@Test
	public void testConstructByteMatcherArray() {
		for (int testNo = 0; testNo < 10; testNo++) {
			final ByteMatcher[] array = createRandomByteMatcherArray(1024);
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(array);
			assertEquals("length:" + Integer.toString(array.length), array.length, matcher.length());

			for (int i = 0; i < array.length; i++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(i);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				final byte matchingValue = array[i].getMatchingBytes()[0];
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("byte value:" + Integer.toString(matchingValue), matchingValue, matchingBytes[0]);
			}

			testDefensivelyCopied(array, matcher);
		}
	}

	private void testDefensivelyCopied(ByteMatcher[] array, SequenceMatcher matcher) {
		changeArray(array);
		for (int pos = 0; pos < array.length; pos++) {
			final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
			final byte[] matchingBytes = sbm.getMatchingBytes();
			final byte matchingValue = array[pos].getMatchingBytes()[0];
			assertEquals("number of bytes matched=1", 1, matchingBytes.length);
			assertTrue("byte value not equals:" + Integer.toString(matchingValue), matchingValue != matchingBytes[0]);
		}
	}

	private void changeArray(ByteMatcher[] array) {
		for (int i = 0; i < array.length; i++) {
			int newvalue = ((array[i].getMatchingBytes()[0] & 0xFF) + 1) % 256;
			array[i] = OneByteMatcher.valueOf((byte) newvalue);
		}
	}
	

	/**
	 * Construct using random repeated byte values for all byte values. Tests
	 * are:
	 * 
	 * - length is correct - each position in the matcher only matches one byte.
	 * - each byte in the matcher is correct.
	 * 
	 */
	@Test
	public void testConstructRepeatedBytes() {
		for (int byteValue = 0; byteValue < 256; byteValue++) {
			final int repeats = rand.nextInt(1024) + 1;
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(repeats,
					OneByteMatcher.valueOf((byte) byteValue));
			assertEquals("length:" + Integer.toString(repeats) + ", byte value:" + Integer.toString(byteValue),
					repeats, matcher.length());

			for (int pos = 0; pos < repeats; pos++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("byte value:" + Integer.toString(byteValue), byteValue, matchingBytes[0] & 0xFF);
			}
		}
	}
	

	@Test
	public void testRepeatedByteMatcher() {
		for (int byteValue = 0; byteValue < 256; byteValue++) {
			final int repeats = rand.nextInt(1024) + 1;
			ByteMatcher bytematcher = OneByteMatcher.valueOf((byte) byteValue);
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(repeats, bytematcher);
			assertEquals("length:" + Integer.toString(repeats) + ", byte value:" + Integer.toString(byteValue),
					repeats, matcher.length());

			for (int pos = 0; pos < repeats; pos++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("byte value:" + Integer.toString(byteValue), byteValue, matchingBytes[0] & 0xFF);
			}
		}
	}


	@Test
	public void testConstructSingleByteMatcher() {
		for (int i = 0; i < 256; i++) {
			ByteMatcher matcher = OneByteMatcher.valueOf((byte) i);
			SequenceSequenceMatcher sequence = new SequenceSequenceMatcher(matcher);
			assertEquals("Length is one", 1, sequence.length());
			byte[] test = new byte[] { (byte) i };
			assertTrue("Matches byte value correctly " + i, sequence.matches(test, 0));
			int val2 = (i + 1) % 256;
			byte[] test2 = new byte[] { (byte) val2 };
			assertFalse("Does not match value " + val2, sequence.matches(test2, 0));
		}
	}

	
	@Test
	public void testConstructRepeatedByteMatcherArray() {
		for (int byteValue = 0; byteValue < 256; byteValue++) {
			final int repeats = rand.nextInt(1024) + 1;
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(repeats,
					OneByteMatcher.valueOf((byte) byteValue));
			assertEquals("length:" + Integer.toString(repeats) + ", byte value:" + Integer.toString(byteValue),
					repeats, matcher.length());

			for (int pos = 0; pos < repeats; pos++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("byte value:" + Integer.toString(byteValue), byteValue, matchingBytes[0] & 0xFF);
			}
		}
	}

	
	@Test
	public void testConstructRepeatedSequenceSequenceMatcher() {
		for (int testNo = 0; testNo < 10; testNo++) {
			final ByteMatcher[] array = createRandomByteMatcherArray(1024);
			final SequenceSequenceMatcher source = new SequenceSequenceMatcher(array);
			final int repeats = rand.nextInt(10) + 1;
			final int length = array.length * repeats;
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(repeats, source);
			assertEquals("length:" + length, length, matcher.length());

			for (int pos = 0; pos < length; pos++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				final int arrayPos = (pos % array.length);
				final byte matchingValue = array[arrayPos].getMatchingBytes()[0];
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("byte value:" + Integer.toString(matchingValue), matchingValue, matchingBytes[0]);
			}
		}
	}


	@Test
	public void testConstructSingleSequenceSequenceMatcher() {
		ByteMatcher[] array = createRandomByteMatcherArray(65);
		SequenceSequenceMatcher source = new SequenceSequenceMatcher(array);
		SequenceSequenceMatcher result = new SequenceSequenceMatcher(source);
		assertEquals("Lengths are the same: " + array.length, source.length(), result.length());
		for (int i = 0; i < array.length; i++) {
			final ByteMatcher sbm = result.getMatcherForPosition(i);
			final byte[] matchingBytes = sbm.getMatchingBytes();
			final byte matchingValue = array[i].getMatchingBytes()[0];
			assertEquals("number of bytes matched=1", 1, matchingBytes.length);
			assertEquals("byte value:" + Integer.toString(matchingValue), matchingValue, matchingBytes[0]);
		}
	}

	
	@Test
	public void testConstructSequenceSequenceMatcherArray() {
		SequenceSequenceMatcher[] matchers = new SequenceSequenceMatcher[5];
		int totalLength = 0;
		for (int i = 0; i < 5; i++) {
			matchers[i] = new SequenceSequenceMatcher(createRandomByteMatcherArray(129));
			totalLength += matchers[i].length();
		}
		SequenceSequenceMatcher result = new SequenceSequenceMatcher(matchers);
		assertEquals("Length is correct: " + totalLength, totalLength, result.length());

		int matcherPos = 0;
		for (int i = 0; i < 5; i++) {
			SequenceSequenceMatcher component = matchers[i];
			for (int j = 0; j < component.length(); j++) {
				ByteMatcher sourceMatch = component.getMatcherForPosition(j);
				ByteMatcher resultMatch = result.getMatcherForPosition(matcherPos++);
				byte[] sourceBytes = sourceMatch.getMatchingBytes();
				byte[] resultBytes = resultMatch.getMatchingBytes();
				assertEquals("Same number of bytes match", sourceBytes.length, resultBytes.length);
				assertEquals("Matches the same bytes", sourceBytes[0], resultBytes[0]);
			}
		}
	}

	@Test
	public void testConstructSingleSequenceMatcher() {
		byte[] array = createRandomArray(342);
		ByteSequenceMatcher source = new ByteSequenceMatcher(array);
		SequenceSequenceMatcher result = new SequenceSequenceMatcher(source);
		assertEquals("length is correct " + array.length, source.length(), result.length());
		for (int i = 0; i < array.length; i++) {
			final ByteMatcher sbm = result.getMatcherForPosition(i);
			final byte[] matchingBytes = sbm.getMatchingBytes();
			final byte matchingValue = array[i];
			assertEquals("number of bytes matched=1", 1, matchingBytes.length);
			assertEquals("byte value:" + Integer.toString(matchingValue), matchingValue, matchingBytes[0]);
		}
	}

	@Test
	public void testConstructSequenceMatcherArray() {
		ByteSequenceMatcher[] matchers = new ByteSequenceMatcher[5];
		int totalLength = 0;
		for (int i = 0; i < 5; i++) {
			matchers[i] = new ByteSequenceMatcher(createRandomArray(129));
			totalLength += matchers[i].length();
		}
		SequenceSequenceMatcher result = new SequenceSequenceMatcher(matchers);
		assertEquals("Length is correct: " + totalLength, totalLength, result.length());

		int matcherPos = 0;
		for (int i = 0; i < 5; i++) {
			ByteSequenceMatcher component = matchers[i];
			for (int j = 0; j < component.length(); j++) {
				ByteMatcher sourceMatch = component.getMatcherForPosition(j);
				ByteMatcher resultMatch = result.getMatcherForPosition(matcherPos++);
				byte[] sourceBytes = sourceMatch.getMatchingBytes();
				byte[] resultBytes = resultMatch.getMatchingBytes();
				assertEquals("Same number of bytes match", sourceBytes.length, resultBytes.length);
				assertEquals("Matches the same bytes", sourceBytes[0], resultBytes[0]);
			}
		}
	}

	/**
	 * Construct using random lists of byte sequence matchers. Tests are:
	 * 
	 * - the length of an assembled matcher is correct. - each position in the
	 * list of matchers matches only one byte. - each position in the assembled
	 * matcher matches only one byte. - each byte in the assembled matcher is
	 * correct.
	 */
	@Test
	public void testConstructByteSequenceMatcherList() {
		for (int testNo = 0; testNo < 10; testNo++) {
			final List<SequenceSequenceMatcher> list = createRandomList(32);
			int totalLength = 0;
			for (final SequenceMatcher matcher : list) {
				totalLength += matcher.length();
			}
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(list);
			assertEquals("length:" + Integer.toString(totalLength), totalLength, matcher.length());

			int localPos = -1;
			int matchIndex = 0;
			SequenceMatcher currentMatcher = list.get(matchIndex);
			for (int pos = 0; pos < totalLength; pos++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				localPos++;
				if (localPos == currentMatcher.length()) {
					matchIndex++;
					currentMatcher = list.get(matchIndex);
					localPos = 0;
				}
				final ByteMatcher sbm2 = currentMatcher.getMatcherForPosition(localPos);
				final byte[] matchingBytes2 = sbm2.getMatchingBytes();
				assertEquals("number of bytes matched source=1", 1, matchingBytes2.length);
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("byte value:" + Integer.toString(matchingBytes2[0]), matchingBytes2[0], matchingBytes[0]);
			}
		}
	}

	
	/**
	 * Construct using repeated random lists of byte sequence matchers. Tests are:
	 * 
	 * - the length of an assembled matcher is correct. - each position in the
	 * list of matchers matches only one byte. - each position in the assembled
	 * matcher matches only one byte. - each byte in the assembled matcher is
	 * correct.
	 */
	@Test
	public void testConstructRepeatedByteSequenceMatcherList() {
		for (int testNo = 0; testNo < 10; testNo++) {
			final List<SequenceSequenceMatcher> list = createRandomList(32);
			int totalLength = 0;
			for (final SequenceMatcher matcher : list) {
				totalLength += matcher.length();
			}
			final int repeats = rand.nextInt(10) + 1;
			final SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(repeats, list);
			final SequenceSequenceMatcher toRepeat = new SequenceSequenceMatcher(list);
			final SequenceMatcher sameObject = toRepeat.repeat(1);
			assertEquals("Repeated once is the same object", toRepeat, sameObject);
			final SequenceMatcher repeated = toRepeat.repeat(repeats);
			assertEquals("length:", totalLength * repeats, matcher.length());

			int localPos = -1;
			int matchIndex = 0;
			SequenceMatcher currentMatcher = list.get(matchIndex);
			for (int pos = 0; pos < totalLength * repeats; pos++) {
				final ByteMatcher sbm = matcher.getMatcherForPosition(pos);
				final ByteMatcher rbm = repeated.getMatcherForPosition(pos);
				final byte[] matchingBytes = sbm.getMatchingBytes();
				final byte[] repeatedBytes = rbm.getMatchingBytes();
				localPos++;
				if (localPos == currentMatcher.length()) {
					matchIndex++;
					if (matchIndex == list.size()) {
						matchIndex = 0;
					}
					currentMatcher = list.get(matchIndex);
					localPos = 0;
				}
				final ByteMatcher sbm2 = currentMatcher.getMatcherForPosition(localPos);
				final byte[] matchingBytes2 = sbm2.getMatchingBytes();
				assertEquals("number of bytes matched source=1", 1, matchingBytes2.length);
				assertEquals("number of bytes matched=1", 1, matchingBytes.length);
				assertEquals("number of bytes matched=1", 1, repeatedBytes.length);
				assertEquals("byte value:" + Integer.toString(matchingBytes2[0]), matchingBytes2[0], matchingBytes[0]);
				assertEquals("byte value:" + Integer.toString(matchingBytes2[0]), matchingBytes2[0], repeatedBytes[0]);
			}
		}
	}
	
	
	// ////////////////////////////////
	// construction failure tests //
	// ////////////////////////////////

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNoRepeats() {
		new SequenceSequenceMatcher(0, OneByteMatcher.valueOf((byte) 0x8f));
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullArray() {
		new SequenceSequenceMatcher((SequenceMatcher[]) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructEmptyArray() {
		new SequenceSequenceMatcher(new SequenceMatcher[0]);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullList() {
		new SequenceSequenceMatcher((ArrayList<SequenceSequenceMatcher>) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructEmptyList() {
		new SequenceSequenceMatcher(new ArrayList<SequenceSequenceMatcher>());
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullElementList() {
		List<SequenceSequenceMatcher> list = new ArrayList<SequenceSequenceMatcher>();
		list.add(null);
		new SequenceSequenceMatcher(list);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullByteMatcherCollection() {
		new SequenceSequenceMatcher((List<ByteMatcher>) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructEmptyByteMatcherCollection() {
		new SequenceSequenceMatcher(new ArrayList<ByteMatcher>());
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructByteMatcherCollectionNullElement() {
		List<ByteMatcher> matchers = new ArrayList<ByteMatcher>();
		matchers.add(OneByteMatcher.valueOf((byte) 0x00));
		matchers.add(null);
		new SequenceSequenceMatcher(matchers);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullRepeatedByteMatcher() {
		new SequenceSequenceMatcher(3, (ByteMatcher) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructZeroRepeatedByteMatcher() {
		new SequenceSequenceMatcher(0, OneByteMatcher.valueOf((byte) 0x00));
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNegativeRepeatedByteMatcher() {
		new SequenceSequenceMatcher(-1, OneByteMatcher.valueOf((byte) 0x00));
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullByteMatcher() {
		new SequenceSequenceMatcher((ByteMatcher) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructNullByteMatcherArray() {
		new SequenceSequenceMatcher((ByteMatcher[]) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructRepeatedNullByteMatcherArray() {
		new SequenceSequenceMatcher(1, (ByteMatcher[]) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructZeroRepeatedByteMatcherArray() {
		new SequenceSequenceMatcher(0, new ByteMatcher[] { OneByteMatcher.valueOf((byte) 0x00) });
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructByteMatcherArrayNullElement() {
		new SequenceSequenceMatcher(new ByteMatcher[] { null });
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructEmptyByteMatcherArray() {
		new SequenceSequenceMatcher(new ByteMatcher[0]);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructRepeatedNullByteMatcher() {
		new SequenceSequenceMatcher(5, (ByteMatcher[]) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructRepeatedEmptyByteArray() {
		new SequenceSequenceMatcher(5, new ByteMatcher[0]);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructZeroRepeatedByteArray() {
		new SequenceSequenceMatcher(0, new ByteMatcher[1]);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructRepeatedNullSequenceSequenceMatcher() {
		new SequenceSequenceMatcher(1, (SequenceSequenceMatcher) null);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testConstructZeroRepeatedSequenceSequenceMatcher() {
		new SequenceSequenceMatcher(0, new SequenceSequenceMatcher((byte) 0));
	}

	
	// ///////////////////////////
	// matcher iteration tests //
	// ///////////////////////////

	@Test
	public void testByteMatcherIterator() {
		testByteMatcherIterator("1");
		testByteMatcherIterator("abcdefghijklmnopqrstuvwxyz");
		testByteMatcherIterator("atat\t\tn\n");
	}

	private void testByteMatcherIterator(String value) {
		ByteSequenceMatcher source = new ByteSequenceMatcher(value);
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(source);
		testByteMatcherIterator(matcher, value);

		for (int i = 1; i < value.length() - 1; i++) {
			String newValue = value.substring(i);
			SequenceMatcher submatch = matcher.subsequence(i);
			testByteMatcherIterator(submatch, newValue);
		}

		for (int i = 1; i < value.length(); i++) {
			String newValue = value.substring(0, i);
			SequenceMatcher submatch = matcher.subsequence(0, i);
			testByteMatcherIterator(submatch, newValue);
		}
	}

	private void testByteMatcherIterator(SequenceMatcher value, String expected) {
		Iterator<ByteMatcher> iterator = value.iterator();
		assertNotNull("Byte matcher iterator is not null", iterator);

		try {
			iterator.remove();
			fail("expected an unsupported operation exception removing from the iterator");
		} catch (UnsupportedOperationException ex) {
		}
		;

		assertTrue("Byte matcher iterator must always have at least one element [" + expected + ']', iterator.hasNext());
		int position = 0;
		while (iterator.hasNext()) {
			ByteMatcher bm = iterator.next();
			byte[] matchingbytes = bm.getMatchingBytes();
			assertEquals("Matching bytes has one byte", 1, matchingbytes.length);

			byte b = (byte) expected.charAt(position++);
			assertEquals("Bytes match for byte " + b, b, matchingbytes[0]);
		}

		assertEquals("Lengths match after iteration", position, value.length());

		try {
			iterator.next();
			fail("expected NoSuchElementException");
		} catch (NoSuchElementException ex) {
		}
		;
	}


	// /////////////////////////////
	// reader matching tests //
	// /////////////////////////////

	@Test
	public void testMatches_ByteReader_long() throws FileNotFoundException, IOException {
		SequenceMatcher matcher = new SequenceSequenceMatcher(3, OneByteMatcher.valueOf((byte) 0x2A));
		runTestMatchesAround(matcher, 0, 61, 1017);

		matcher = new SequenceSequenceMatcher(3, OneByteMatcher.valueOf((byte) 0x2A)).reverse();
		runTestMatchesAround(matcher, 0, 61, 1017);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("Here"));
		runTestMatchesAround(matcher, 28200, 60836, 64481);

		matcher = matcher.subsequence(1, 4);
		runTestMatchesAround(matcher, 28201, 60837, 64482);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("ereH")).reverse();
		runTestMatchesAround(matcher, 28200, 60836, 64481);

		matcher = matcher.subsequence(1, 3);
		runTestMatchesAround(matcher, 28201, 60837, 64482);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[] { 0x2e, 0x0d, 0x0a }));
		runTestMatchesAround(matcher, 196, 42004, 112277);

		matcher = matcher.subsequence(1);
		runTestMatchesAround(matcher, 197, 42005, 112278);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[] { 0x0a, 0x0d, 0x2e })).reverse();
		runTestMatchesAround(matcher, 196, 42004, 112277);

		matcher = matcher.subsequence(1);
		runTestMatchesAround(matcher, 197, 42005, 112278);
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
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("be"));
		runTestMatchesAround(matcher, 4095);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("eb")).reverse();
		runTestMatchesAround(matcher, 4095);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("Gutenberg"));
		runTestMatchesAround(matcher, 4090);

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("grebnetuG")).reverse();
		runTestMatchesAround(matcher, 4090);
	}

	// //////////////////////////////////
	// reader out of bounds tests //
	// //////////////////////////////////

	@Test
	public void testMatchesReaderOutOfBoundsNegative() throws IOException {
		for (FileReader reader : readers) {
			SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("xxx"));
			assertFalse("negative position", matcher.matches(reader, -1));
			assertFalse("past end", matcher.matches(reader, 10000000));
	
			matcher = matcher.reverse();
			assertFalse("reverse negative position", matcher.matches(reader, -1));
			assertFalse("reverse past end", matcher.matches(reader, 10000000));
		}
	}

	@Test
	public void testMatchesReaderOutOfBoundsCrossingEnd() throws IOException {
		for (FileReader reader : readers) {
			SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[] { 0x65, 0x2e, 0x0d,
					0x0a, 0x00 }));
			assertFalse("longer than end", matcher.matches(reader, 112276));

			matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(new byte[] { 0x00, 0x0a, 0x0d, 0x2e, 0x65 }))
				.reverse();
			assertFalse("reverse longer than end", matcher.matches(reader, 112276));
		}
	}

	// ///////////////////////////////
	// byte array matches tests //
	// ///////////////////////////////

	@Test
	public void testMatches_byteArr_int() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(3, OneByteMatcher.valueOf((byte) 0x2A));
		runTestMatchesAroundArray(matcher, 0, 61, 1017);

		matcher = new SequenceSequenceMatcher(3, OneByteMatcher.valueOf((byte) 0x2A)).reverse();
		runTestMatchesAroundArray(matcher, 0, 61, 1017);
	}

	@Test
	public void testMatchesNoBoundsCheck_byteArr_int() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(3, OneByteMatcher.valueOf((byte) 0x2A));
		testMatchesAroundArrayNoCheck(matcher, 61);
		testMatchesAroundArrayNoCheck(matcher, 1017);

		matcher = new SequenceSequenceMatcher(3, OneByteMatcher.valueOf((byte) 0x2a)).reverse();
		testMatchesAroundArrayNoCheck(matcher, 61);
		testMatchesAroundArrayNoCheck(matcher, 1017);
	}

	// //////////////////////////////////////
	// byte array out of bounds tests //
	// //////////////////////////////////////

	@Test
	public void testMatches_outOfBoundsNegative() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("Titania"));
		assertFalse("matches at negative pos", matcher.matches(bytes, -1));

		matcher = matcher.reverse();
		assertFalse("matches at negative pos", matcher.matches(bytes, -1));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testMatchesNoBoundsCheck_outOfBoundsNegative() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("Oberon"));
		matcher.matchesNoBoundsCheck(bytes, -1);

		matcher = matcher.reverse();
		matcher.matchesNoBoundsCheck(bytes, -1);
	}

	@Test
	public void testMatches_outOfBoundsPastEnd() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("Bottom"));
		assertFalse("matches past end", matcher.matches(bytes, 4096));

		matcher = matcher.reverse();
		assertFalse("matches past end", matcher.matches(bytes, 4096));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testMatchesNoBoundsCheck_outOfBoundsPastEnd() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("Puck"));
		matcher.matchesNoBoundsCheck(bytes, 4096);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testReverseMatchesNoBoundsCheck_outOfBoundsPastEnd() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("kcuP"));
		matcher = matcher.reverse();
		matcher.matchesNoBoundsCheck(bytes, 4096);
	}

	@Test
	public void testMatches_outOfBoundsCrossingEnd() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("be"));
		assertFalse("matches crossing end", matcher.matches(bytes, 4095));

		matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("eb")).reverse();
		assertFalse("matches crossing end", matcher.matches(bytes, 4095));
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testMatchesNoBoundsCheck_outOfBoundsCrossingEnd() {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("be"));
		matcher.matchesNoBoundsCheck(bytes, 4095);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testReverseMatchesNoBoundsCheck_outOfBoundsCrossingEnd() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("eb"));
		matcher = matcher.reverse();
		matcher.matchesNoBoundsCheck(bytes, 4095);
	}

	// /////////////////////////////////
	// representation test methods //
	// /////////////////////////////////

	@Test
	public void testToRegularExpression() {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("abc"));
		assertEquals("reg ex abc", "'abc'", matcher.toRegularExpression(true));
		assertEquals("reg ex abc", "616263", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new TwoByteMatcher((byte) 0x01, (byte) 0x02), new OneByteMatcher(
				(byte) 0x00));
		assertEquals("reg ex [01 02] 00", "[01 02] 00", matcher.toRegularExpression(true));
		assertEquals("reg ex 00 [01 02]", "[0102]00", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new OneByteMatcher((byte) 0x00), new TwoByteMatcher((byte) 0x01,
				(byte) 0x02));
		assertEquals("reg ex 00 [01 02]", "00 [01 02]", matcher.toRegularExpression(true));
		assertEquals("reg ex 00 [01 02]", "00[0102]", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new OneByteMatcher((byte) 0x00), new OneByteMatcher((byte) 0xff),
				new TwoByteMatcher((byte) 0x01, (byte) 0x02), new TwoByteMatcher((byte) 0x03, (byte) 0x04),
				new OneByteMatcher((byte) 0xfe));

		assertEquals("reg ex 00 ff [01 02] [03 04] fe", "00 ff [01 02] [03 04] fe", matcher.toRegularExpression(true));
		assertEquals("reg ex 00 [01 02]", "00ff[0102][0304]fe", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new OneByteMatcher((byte) 0x41), new OneByteMatcher((byte) 0x42),
				new TwoByteMatcher((byte) 0x01, (byte) 0x02), new TwoByteMatcher((byte) 0x03, (byte) 0x04),
				new OneByteMatcher((byte) 0x01));

		assertEquals("reg ex 'AB' [01 02] [03 04] 01", "'AB' [01 02] [03 04] 01", matcher.toRegularExpression(true));
		assertEquals("reg ex 4142[0102][0304]01", "4142[0102][0304]01", matcher.toRegularExpression(false));
	}

	@Test
	public void testReverseToRegularExpression() {
		SequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("abc")).reverse();
		assertEquals("reg ex cba", "'cba'", matcher.toRegularExpression(true));
		assertEquals("reg ex cba", "636261", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new TwoByteMatcher((byte) 0x01, (byte) 0x02), new OneByteMatcher(
				(byte) 0x00)).reverse();
		assertEquals("reg ex 00 [01 02]", "00 [01 02]", matcher.toRegularExpression(true));
		assertEquals("reg ex 00 [01 02]", "00[0102]", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new OneByteMatcher((byte) 0x00), new TwoByteMatcher((byte) 0x01,
				(byte) 0x02)).reverse();
		assertEquals("reg ex [01 02] 00", "[01 02] 00", matcher.toRegularExpression(true));
		assertEquals("reg ex [0102]00", "[0102]00", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new OneByteMatcher((byte) 0x00), new OneByteMatcher((byte) 0xff),
				new TwoByteMatcher((byte) 0x01, (byte) 0x02), new TwoByteMatcher((byte) 0x03, (byte) 0x04),
				new OneByteMatcher((byte) 0xfe)).reverse();

		assertEquals("reg ex fe [03 04] [01 02] ff 00", "fe [03 04] [01 02] ff 00", matcher.toRegularExpression(true));
		assertEquals("reg ex fe[0304][0102]ff00", "fe[0304][0102]ff00", matcher.toRegularExpression(false));

		matcher = new SequenceSequenceMatcher(new OneByteMatcher((byte) 0x41), new OneByteMatcher((byte) 0x42),
				new TwoByteMatcher((byte) 0x01, (byte) 0x02), new TwoByteMatcher((byte) 0x03, (byte) 0x04),
				new OneByteMatcher((byte) 0x01)).reverse();

		assertEquals("reg ex 01 [03 04] [01 02] 'BA'", "01 [03 04] [01 02] 'BA'", matcher.toRegularExpression(true));
		assertEquals("reg ex 01[0304][0102]4241", "01[0304][0102]4241", matcher.toRegularExpression(false));
	}

	@Test
	public void testToString() {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher("abc"));
		assertTrue("String contains class name", matcher.toString().contains("SequenceSequenceMatcher"));
		assertTrue("String contains data", matcher.toString().contains("abc"));
	}

	@Test
	public void testGetByteMatcherForPosition() {
		testMatchersForSequence("abc");
		testMatchersForSequence("x");
		testMatchersForSequence("Midsommer");
		testMatchersForSequence("testGetByteMatcherForPosition");
	}

	// ///////////////////////
	// view test methods //
	// ///////////////////////

	@Test
	public void testReverse() {
		testReversed("a");
		testReversed("abcdefg");
		testReversed("xx");
		testReversed("1234567890abcdefghijklmnopqrstuvwxyz");
	}

	@Test
	public void testSubsequence() {
		testSubSequence(" ");
		testSubSequence("abc");
		testSubSequence("I know a banke where the wilde thyme blows");
		testSubSequence("xx");
		testSubSequence("\tq\tw\te\tr\ni\r\t\t \tx\t.");
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testNegativeSubsequenceBeginIndex() {
		SequenceSequenceMatcher test = new SequenceSequenceMatcher(new ByteSequenceMatcher("0123456789"));
		test.subsequence(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSubsequenceEndIndexTooBig() {
		SequenceSequenceMatcher test = new SequenceSequenceMatcher(new ByteSequenceMatcher("0123456789"));
		test.subsequence(0, 11);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSubsequenceStartIndexTooBig() {
		SequenceSequenceMatcher test = new SequenceSequenceMatcher(new ByteSequenceMatcher("0123456789"));
		test.subsequence(5, 5);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRepeatNotZero() {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher((byte) 0x01);
		matcher.repeat(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRepeatNotNegative() {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher((byte) 0x01);
		matcher.repeat(-1);
	}


	// ////////////////////////////////////////////
	// standard sequence matcher test methods //
	// ////////////////////////////////////////////

	@Test(expected = IndexOutOfBoundsException.class)
	public void testNegativeByteMatcherPosition() {
		SequenceSequenceMatcher test = new SequenceSequenceMatcher(new ByteSequenceMatcher("0123456789"));
		test.getMatcherForPosition(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testNegativeByteMatcherSubsequencePosition() {
		SequenceSequenceMatcher test = new SequenceSequenceMatcher(new ByteSequenceMatcher("01234"),
				new ByteSequenceMatcher("56789"));

		SequenceMatcher testSub = test.subsequence(3);
		testSub.getMatcherForPosition(-1);
	}

	// //////////////////////////
	// private test methods //
	// //////////////////////////

	private void testSubSequence(String sequence) {
		testSubSequenceBeginIndex(sequence);
		testSubSequenceBeginEndIndex(sequence);
	}

	private void testSubSequenceBeginIndex(String sequence) {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(sequence));

		// Test all possible subsequences of it using only beginIndex:
		SequenceMatcher sub = matcher;
		for (int count = 1; count < sequence.length(); count++) {
			sub = sub.subsequence(1);
			assertEquals("subsequence length correct", sequence.length() - count, sub.length());
			for (int pos = count; pos < sequence.length(); pos++) {
				int charvalue = sequence.charAt(pos);
				byte[] matchingbytes = sub.getMatcherForPosition(pos - count).getMatchingBytes();
				assertEquals("only one byte matches at position", 1, matchingbytes.length);
				assertEquals("values correct at pos " + pos, charvalue, (matchingbytes[0] & 0xFF));
			}
		}

		// Now run equivalent tests for the reversed sequence:
		sub = matcher.reverse();
		for (int count = 1; count < sequence.length(); count++) {
			sub = sub.subsequence(1);
			assertEquals("subsequence length correct", sequence.length() - count, sub.length());
			for (int pos = count; pos < sequence.length(); pos++) {
				int charvalue = sequence.charAt(sequence.length() - pos - 1);
				byte[] matchingbytes = sub.getMatcherForPosition(pos - count).getMatchingBytes();
				assertEquals("only one byte matches at position", 1, matchingbytes.length);
				assertEquals("values correct at pos " + pos, charvalue, (matchingbytes[0] & 0xFF));
			}
		}

	}

	private void testSubSequenceBeginEndIndex(String sequence) {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(sequence));

		// Test all possible subsequences of it from begin index to end index:
		SequenceMatcher sub = matcher;
		for (int beginIndex = 0; beginIndex < sequence.length(); beginIndex++) {
			for (int endIndex = beginIndex + 1; endIndex <= sequence.length(); endIndex++) {
				sub = matcher.subsequence(beginIndex, endIndex);
				int sequencelength = endIndex - beginIndex;
				assertEquals("subsequence length correct", sequencelength, sub.length());
				for (int pos = 0; pos < sequencelength; pos++) {
					int charvalue = sequence.charAt(beginIndex + pos);
					byte[] matchingbytes = sub.getMatcherForPosition(pos).getMatchingBytes();
					assertEquals("only one byte matches at position", 1, matchingbytes.length);
					assertEquals("values correct at pos " + pos, charvalue, (matchingbytes[0] & 0xFF));
				}
			}
		}

		// Now run equivalent tests for the reversed sequence:
		SequenceMatcher reversed = matcher.reverse();
		for (int beginIndex = 0; beginIndex < sequence.length(); beginIndex++) {
			for (int endIndex = beginIndex + 1; endIndex <= sequence.length(); endIndex++) {
				sub = reversed.subsequence(beginIndex, endIndex);
				int sequencelength = endIndex - beginIndex;
				assertEquals("subsequence length correct", sequencelength, sub.length());
				for (int pos = 0; pos < sequencelength; pos++) {
					int charvalue = sequence.charAt(sequence.length() - beginIndex - pos - 1);
					byte[] matchingbytes = sub.getMatcherForPosition(pos).getMatchingBytes();
					assertEquals("only one byte matches at position", 1, matchingbytes.length);
					assertEquals("values correct at pos " + pos + " for sequence " + sequence + " begin at "
							+ beginIndex + " end at " + endIndex, charvalue, (matchingbytes[0] & 0xFF));
				}
			}
		}

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
	 * - the length of a reversed sequence is the same as the original. - that
	 * each position in the reversed matcher matches only one byte. - that the
	 * bytes in the reversed matcher correspond to the original, but reversed.
	 * 
	 * @param sequence
	 *            A string to construct a SequenceSequenceMatcher from.
	 */
	private void testReversed(String sequence) {
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(sequence));
		SequenceMatcher reversed = matcher.reverse();
		int matcherLength = matcher.length();
		assertEquals(sequence + " length", matcherLength, reversed.length());

		for (int index = 0; index < matcherLength; index++) {
			byte[] matcherbytes = matcher.getMatcherForPosition(index).getMatchingBytes();
			assertEquals(sequence + " matches one byte at index" + index, 1, matcherbytes.length);
			byte[] reversebytes = reversed.getMatcherForPosition(reversed.length() - index - 1).getMatchingBytes();
			assertArrayEquals(sequence + " bytes match", matcherbytes, reversebytes);
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
		for (FileReader reader : readers) {
			assertTrue(matchDesc + " at pos " + Long.toString(pos), matcher.matches(reader, pos));
			assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(reader, pos - 1));
			assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(reader, pos + 1));
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
		assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matches(bytes, pos - 1));
		assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matches(bytes, pos + 1));
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
		assertFalse(matchDesc + " at pos " + Long.toString(pos - 1), matcher.matchesNoBoundsCheck(bytes, pos - 1));
		assertFalse(matchDesc + " at pos " + Long.toString(pos + 1), matcher.matchesNoBoundsCheck(bytes, pos + 1));
	}

	/**
	 * Tests that:
	 * 
	 * - a matcher is the right length with the right byte values for a string
	 * sequence. - the same holds true for the reverse matcher on the reversed
	 * string.
	 * 
	 * @param sequence
	 */
	private void testMatchersForSequence(String sequence) {
		// test forwards matcher
		SequenceSequenceMatcher matcher = new SequenceSequenceMatcher(new ByteSequenceMatcher(sequence));
		testByteMatcherForPosition(sequence, matcher);

		// test the reversed matcher
		SequenceMatcher reversed = matcher.reverse();
		String reverseSequence = new StringBuffer(sequence).reverse().toString();
		testByteMatcherForPosition(reverseSequence, reversed);
	}

	/**
	 * Tests that:
	 * 
	 * - each position in a byte matcher constructed from a string matches only
	 * one byte. - the value is the corresponding value in the original string.
	 * 
	 * @param sequence
	 * @param m
	 */
	private void testByteMatcherForPosition(String sequence, SequenceMatcher m) {
		for (int position = 0; position < sequence.length(); position++) {
			byte[] onebytes = m.getMatcherForPosition(position).getMatchingBytes();
			assertEquals(sequence + " length", 1, onebytes.length);
			assertEquals(sequence + "value", sequence.charAt(position), onebytes[0] & 0xFF);
		}
	}

	// /////////////////////////////
	// private utility methods //
	// /////////////////////////////

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
	
	private List<FileReader> getReaders(String resourceName, int... windowFromTo) throws FileNotFoundException {
		List<FileReader> result = new ArrayList<FileReader>(32);
		for (int pos = 0; pos < windowFromTo.length; pos += 2) {
			int windowSizeFrom = windowFromTo[pos];
			int windowSizeTo   = windowFromTo[pos + 1];
			for (int windowSize = windowSizeFrom; windowSize <= windowSizeTo; windowSize++) {
				result.add(new FileReader(getFile(resourceName), windowSize));
			}
		}
		return result;
	}
	
	/**
	 * Creates a random length byte array containing random bytes.
	 * 
	 * @param maxLength
	 * @return
	 */
	private byte[] createRandomArray(final int maxLength) {
		final int length = rand.nextInt(maxLength) + 1;
		final byte[] array = new byte[length];
		for (int pos = 0; pos < length; pos++) {
			array[pos] = (byte) rand.nextInt(256);
		}
		return array;
	}

	/**
	 * Creates a random length ByteMatcher array containing random bytes.
	 * 
	 * @param maxLength
	 * @return
	 */
	private ByteMatcher[] createRandomByteMatcherArray(final int maxLength) {
		final int length = rand.nextInt(maxLength) + 1;
		final ByteMatcher[] array = new ByteMatcher[length];
		for (int pos = 0; pos < length; pos++) {
			array[pos] = OneByteMatcher.valueOf((byte) rand.nextInt(256));
		}
		return array;
	}

	/**
	 * Creates a random length list of random length matchers. The matchers are
	 * constructed using either a random byte value, a random length byte array,
	 * or a random number of repeated random byte values.
	 * 
	 * @param maxNum
	 * @return
	 */
	private List<SequenceSequenceMatcher> createRandomList(final int maxNum) {
		final int noOfMatchers = rand.nextInt(maxNum) + 1;
		final List<SequenceSequenceMatcher> matchers = new ArrayList<SequenceSequenceMatcher>();
		for (int num = 0; num < noOfMatchers; num++) {
			final int matchType = rand.nextInt(3);
			SequenceSequenceMatcher matcher;
			switch (matchType) {
			case 0: {
				final int byteValue = rand.nextInt(256);
				matcher = new SequenceSequenceMatcher(OneByteMatcher.valueOf((byte) byteValue));
				break;
			}
			case 1: {
				final ByteMatcher[] values = createRandomByteMatcherArray(256);
				matcher = new SequenceSequenceMatcher(values);
				break;
			}
			case 2: {
				final int byteValue = rand.nextInt(256);
				final int repeats = rand.nextInt(256) + 1;
				matcher = new SequenceSequenceMatcher(repeats, OneByteMatcher.valueOf((byte) byteValue));
				break;
			}
			default: {
				throw new IllegalArgumentException("Invalid matcher type");
			}
			}
			matchers.add(matcher);
		}
		return matchers;
	}

}
