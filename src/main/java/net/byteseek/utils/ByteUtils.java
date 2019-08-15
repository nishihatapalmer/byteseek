/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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

package net.byteseek.utils;

import net.byteseek.parser.ParseException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A utility class containing useful methods to work with bytes, including:
 * <ul>
 * <li>Translating between arrays and collections of bytes
 * <li>Counting bits in bytes.
 * <li>Counting permutations of bytes given a bit mask matching any or all bits.
 * <li>Returning the set of bytes matching a bit mask (on any or all of them).
 * </ul>
 * 
 * @author Matt Palmer
 */
public final class ByteUtils {
	
	private static final String CHAR_BYTE_FORMAT = "'%c'";
	private static final String HEX_BYTE_FORMAT = "%02x";

	private static final byte ASCII_CASE_DIFFERENCE = 32;
    
	private static final int QUOTE_CHARACTER_VALUE = 39;
    private static final int START_PRINTABLE_ASCII = 32;
    private static final int END_PRINTABLE_ASCII = 126;

    private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    public static final int ASCII_CASE_GAP = 'a' - 'A'; // The distance between upper and lower case in ASCII chars.
    public static final byte LINE_FEED = 0x0A;
    public static final byte CARRIAGE_RETURN = 0x0D;

    /**
     * Returns the number of bits set in a given byte.
     * 
     * Algorithm taken from:
     * http://www-graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
     * 
     * @param b The byte to count the set bits.
     * @return The number of bits set in the byte.
     */
    public static int countSetBits(final byte b) {
        final int bits = b & 0xFF;
        int result = bits - ((bits >>> 1) & 0x55);
        result = ((result >>> 2) & 0x33) + (result & 0x33);
        return ((result >>> 4) + result) & 0x0F;
    }

    /**
     * Returns the number of unset bits in a given byte.
     *
     * @param b The byte to count the unset bits.
     * @return The number of bits unset in the byte.
     */
    public static int countUnsetBits(final byte b) {
        return 8 - countSetBits(b);
    }

    /**
     * Returns the number of bytes which would match all the bits
     * in a given bitmask.
     * <p>
     * Note that if the bitmask is zero, then this will match all bytes, since
     * the matching algorithm is byte &amp; bitmask == bitmask.
     * A bitmask of zero will always produce zero bits when ANDed with any byte.
     *
     * @param bitmask The bitmask.
     * @return The number of bytes matching all the bits in the bitmask.
     */
    public static int countBytesMatchingAllBits(final byte bitmask) {
    	return 1 << countUnsetBits(bitmask);
    }

    //TODO: add count methods for wildbit and wildbit any

    /**
     * Returns the number of bytes which would match any of the bits
     * in a given bitmask.
     *
     * @param bitmask The bitmask.
     * @return The number of bytes matching any of the bits in the bitmask.
     */
    public static int countBytesMatchingAnyBit(final byte bitmask) {
        return 256 - countBytesMatchingAllBits(bitmask);
    }

    /**
     * Returns the number of bytes which would many any of the set bits in a mask, also taking into account
     * whether matching should be inverted or not.
     *
     * @param bitmask The bitmask
     * @param isInverted Whether to invert the results or not.
     * @return the number of bytes which would match any of the set bits in a mask, also taking into account inversion.
     */
    public static int countBytesMatchingAnyBits(final byte bitmask, final boolean isInverted) {
        final int numBytesMatchingMask = bitmask == 0? 256 : 256 - (1 << countUnsetBits(bitmask));
        return isInverted? 256 - numBytesMatchingMask : numBytesMatchingMask;
    }

    /**
     * Returns a byte array containing the byte values which match an all bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that matched the all bitmask.
     */
    public static byte[] getBytesMatchingAllBitMask(final byte bitMask) {
    	final int numberOfBytes = countBytesMatchingAllBits(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) == bitMask) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }

    /**
     * Returns a byte array containing the byte values which do not match an all bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that did not match the all bitmask.
     */
    public static byte[] getBytesNotMatchingAllBitMask(final byte bitMask) {
    	final int numberOfBytes = 256 - countBytesMatchingAllBits(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) != bitMask) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }
    
    /**
     * Returns a byte array containing the byte values which match an any bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that matched the any bitmask.
     */
    public static byte[] getBytesMatchingAnyBitMask(final byte bitMask) {
    	final int numberOfBytes = countBytesMatchingAnyBit(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) != 0) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }

    /**
     * Returns a byte array containing the byte values which do not match an any bitmask.
     * 
     * @param bitMask The bitmask to match.
     * @return An array of bytes containing the bytes that did not match the any bitmask.
     */
    public static byte[] getBytesNotMatchingAnyBitMask(final byte bitMask) {
    	final int numberOfBytes = 256 - countBytesMatchingAnyBit(bitMask);
    	final byte[] bytes = new byte[numberOfBytes];
    	int arrayCount = 0;
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
    		if ((((byte) byteIndex) & bitMask) == 0) {
    			bytes[arrayCount++] = (byte) byteIndex;
    		}
    	}
    	return bytes;
    }

    /**
     * Adds the bytes which would match all the bits in a given bitmask to a 
     * Collection of Byte.
     *
     * @param bitMask The bitmask to match.
     * @param bytes The collection of bytes to add the Bytes to.
     * @throws IllegalArgumentException if the collection of bytes passed in is null.
     */
    public static void addBytesMatchingAllBitMask(final byte bitMask, 
    											  final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	final int numToAdd = countBytesMatchingAllBits(bitMask);
    	for (int byteIndex = 0, numAdded = 0; numAdded < numToAdd && byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((((byte) byteIndex) & bitMask) == bitMask) {
                bytes.add(Byte.valueOf(byteValue));
                numAdded++;
            }
        }
    }
    
	/**
	 * Adds all possible bytes to a collection of Byte.
	 * 
	 * @param bytes A collection of bytes to add all possible bytes to.
	 * @throws IllegalArgumentException if the collection of bytes passed in is null.
	 */
	public static void addAllBytes(final Collection<Byte> bytes) {
		ArgUtils.checkNullCollection(bytes);
    	for (int i = 0; i < 256; i++) {
			bytes.add(Byte.valueOf((byte) i));
		}
	}

    /**
     * Adds the bytes not matching an all-bit bitmask to a collection of Byte.
     *
     * @param bitMask The bitmask to not match.
     * @param bytes The collection of bytes to add the bytes to.
	 * @throws IllegalArgumentException if the collection of bytes passed in is null. 
     */
    public static void addBytesNotMatchingAllBitMask(final byte bitMask,
    												 final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
        final int numToAdd = 256 - countBytesMatchingAllBits(bitMask);
        for (int byteIndex = 0, numAdded = 0; numAdded < numToAdd && byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((((byte) byteIndex) & bitMask) != bitMask) {
                bytes.add(Byte.valueOf(byteValue));
                numAdded++;
            }
        }
    }

    /**
     * Returns a set of bytes from an array of bytes.
     * 
     * @param bytes The array of bytes.
     * @return A set of bytes.
     * @throws IllegalArgumentException if the byte array is null.
     */
    public static Set<Byte> toSet(final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
        final Set<Byte> setOfBytes = new HashSet<Byte>((int) (bytes.length / 0.75));
        addAll(bytes, setOfBytes);
        return setOfBytes;
    }

    /**
     * Returns a list of bytes from an array of bytes.
     * 
     * @param bytes The array of bytes
     * @return A list of bytes
     * @throws IllegalArgumentException if the byte array is null. 
     */
    public static List<Byte> toList(final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
        final List<Byte> listOfBytes = new ArrayList<Byte>(bytes.length);
        for (final byte b : bytes) {
            listOfBytes.add(Byte.valueOf(b));
        }
        return listOfBytes;
    }

    /**
     * Adds all the bytes in an array to a collection of Bytes.
     * 
     * @param bytes The array of bytes to add.
     * @param toCollection The collection of Bytes to add to.
     * @throws IllegalArgumentException if the byte array or collection is null.
     */
    public static void addAll(final byte[] bytes, final Collection<Byte> toCollection) {
        ArgUtils.checkNullByteArray(bytes);
        ArgUtils.checkNullCollection(toCollection);
    	final int size = bytes.length;
        for (int count = 0; count < size; count++) {
            toCollection.add(Byte.valueOf(bytes[count]));
        }
    }

    /**
     * Adds all the bytes specified as byte parameters to the collection.
     * @param toCollection The collection to add the bytes to.
     * @param values The byte values as parameters.
     * @throws IllegalArgumentException if the collection is null.
     */
    public static void addBytes(final Collection<Byte> toCollection, final byte...values) {
    	ArgUtils.checkNullCollection(toCollection);
    	for (final byte value : values) {
    		toCollection.add(Byte.valueOf(value));
    	}
    }
    
    /**
     * Adds the bytes in a string encoded as ISO-8859-1 bytes to a collection of bytes.
     * 
     * @param string The ISO-8859-1 string whose bytes should be added
     * @param toCollection The collection to add the bytes to.
     * @throws IllegalArgumentException if the string or collection is null.
     */
    public static void addStringBytes(final String string, final Collection<Byte> toCollection) {
    	addAll(getBytes(string), toCollection);
    }

    /**
     * Returns a byte array of the string passed in, encoded as ISO-8859-1.
     * <p>
     * If the string cannot be encoded as ISO-8859-1, then the behaviour of this
     * method is undefined.
     * 
     * @param string The string to convert to a byte array encoded as ISO-8859-1
     * @return The byte array representing the string encoded as ISO-8859-1
     * @throws IllegalArgumentException if the string passed in is null.
     */
    public static byte[] getBytes(final String string) {
    	ArgUtils.checkNullString(string);
   		return string.getBytes(ISO_8859_1);
    }

    /**
     * Adds the bytes in a string encoded as ISO-8859-1 to a collection of bytes.
     * Upper and lower case bytes are also added if their counterpart is encountered.
     * 
     * @param string The ISO-8859-1 string whose bytes should be added
     * @param toCollection The collection to add the bytes to.
     * @throws IllegalArgumentException if the string or collection passed in is null.
     */
    public static void addCaseInsensitiveStringBytes(final String string, final Collection<Byte> toCollection) {
    	ArgUtils.checkNullCollection(toCollection);
    	final byte[] byteValues = getBytes(string);
		for (int charIndex = 0; charIndex < byteValues.length; charIndex++) {
			final byte charAt = byteValues[charIndex];
			if (charAt >= 'a' && charAt <= 'z') {
				toCollection.add(Byte.valueOf((byte) (charAt - ASCII_CASE_DIFFERENCE)));
			} else if (charAt >= 'A' && charAt <= 'Z') {
				toCollection.add(Byte.valueOf((byte) (charAt + ASCII_CASE_DIFFERENCE)));
			}
			toCollection.add(Byte.valueOf(charAt));
		}
    }
    
    /**
     * Returns an array of bytes from a collection of Bytes.
     * 
     * @param collection The collection of bytes to convert to an array.
     * @return An array of bytes
     * @throws IllegalArgumentException if the collection passed in is null.
     */
    public static byte[] toArray(final Collection<Byte> collection) {
    	ArgUtils.checkNullCollection(collection);
    	final byte[] result = new byte[collection.size()];
        int position = 0;
        for (final Byte b : collection) {
            result[position++] = b;
        }
        return result;
    }

    /**
     * Returns an array of bytes from a list of byte parameters.
     * 
     * @param values The byte parameters
     * @return A byte array of the parameters.
     */
    public static byte[] toArray(byte... values) {
    	return values;
    }

    /**
     * Reverses an array of bytes.
     * 
     * @param array The array of bytes to reverse.
     * @return byte[] The reversed array of bytes.
     * @throws IllegalArgumentException if the array passed in is null.
     */
    public static byte[] reverseArray(final byte[] array) {
    	ArgUtils.checkNullByteArray(array);
        final int lastpos = array.length - 1;
        final byte[] reversed = new byte[array.length];
        for (int i = 0; i <= lastpos; i++) {
            reversed[i] = array[lastpos - i];
        }
        return reversed;
    }
    
    /**
     * Reverses a subsequence of an array.
     * 
     * @param array The array to reverse a subsequence of.
     * @param startIndex The start position in the array, inclusive.
     * @param endIndex The end index in the array, exclusive.
     * @return A new array containing the bytes of the original array from the
     *         start index to the end index, in reverse order.
     * @throws IllegalArgumentException if the array is null or the indexes are outside of the array.
     */
    public static byte[] reverseArraySubsequence(final byte[] array, final int startIndex, final int endIndex) {
        ArgUtils.checkBounds(array, startIndex, endIndex);
    	final int length = endIndex - startIndex;
        final int endPos = endIndex - 1;
        final byte[] reversed = new byte[length];
        for (int i = 0; i < length; i++) {
            reversed[i] = array[endPos - i];
        }
        return reversed;        
    }
    
    /**
     * Returns a byte array containing the original array passed in repeated a 
     * number of times.  It will always create a new array, even if the number of
     * times to repeat is only one.
     * @param numberOfRepeats The number of times to repeat the array.
     * @param array The array to repeat.
     * 
     * @return A new array containing the original array repeated a number of time.
     * @throws IllegalArgumentException if the array is null or the number of repeats is negative.
     */
    public static byte[] repeat(final int numberOfRepeats, final byte[] array) {
    	ArgUtils.checkNullByteArray(array);
    	ArgUtils.checkNegativeRepeats(numberOfRepeats);
    	final int repeatLength = array.length;
        final int size = repeatLength * numberOfRepeats;
        final byte[] repeated = new byte[size];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(array, 0, repeated, repeat * repeatLength, repeatLength);
        }    
        return repeated;
    }

    /**
     * Returns a byte array containing the original array passed in, 
     * with a subsequence of it repeated a number of times.  
     * It will always produce a new array, even if the number of repeats is only one.
     * @param numberOfRepeats The number of times to repeat the subsequence.
     * @param array The array to repeat a subsequence of.
     * @param startIndex The start index to begin repeating the array from, inclusive.
     * @param endIndex The end index to stop repeating the array from, exclusive.
     * 
     * @return A new byte array consisting of the portions of the original array
     *         from the startIndex to the endIndex repeated.
     * @throws IllegalArgumentException if the array passed in is null, the number of repeats
     *         is negative, or the indexes are out of bounds for the array.
     */
    public static byte[] repeat(final int numberOfRepeats, final byte[] array,
                                final int startIndex, final int endIndex) {
    	ArgUtils.checkBounds(array, startIndex, endIndex);
    	ArgUtils.checkNegativeRepeats(numberOfRepeats);
    	final int repeatLength = endIndex - startIndex;
        final int size = repeatLength * numberOfRepeats;
        final byte[] repeated = new byte[size];
        for (int repeat = 0; repeat < numberOfRepeats; repeat++) {
            System.arraycopy(array, startIndex, repeated, repeat * repeatLength, repeatLength);
        }    
        return repeated;
    }    

    /**
     * Returns a byte array filled with the value for the number of repeats.
     * 
     * @param value The value to repeat
     * @param numberOfRepeats The number of times to repeat the value.
     * @return A byte array sized to the number of repeats filled with the value.
     * @throws IllegalArgumentException if the number of repeats is negative.
     */
    public static byte[] repeat(final byte value, final int numberOfRepeats) {
        ArgUtils.checkNegativeRepeats(numberOfRepeats);
        final byte[] repeats = new byte[numberOfRepeats];
        Arrays.fill(repeats, value);
        return repeats;
    }

    /**
     * Converts an array of bytes to an array of ints in the range 0 to 255.
     * 
     * @param bytes The byte array.
     * @return int[] The integer array.
     * @throws IllegalArgumentException if the byte array is null.
     */
    public static int[] toIntArray(final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
        final int length = bytes.length;
    	final int[] integers = new int[length];
        for (int index = 0; index < length; index++) {
            integers[index] = bytes[index] & 0xFF;
        }
        return integers;
    }

    /**
     * Returns an array of bytes containing all possible byte values.
     * 
     * @return byte[] The array of bytes.
     */
    public static byte[] getAllByteValues() {
        return getBytesInRange(0, 255);
    }

    /**
     * Returns an array of bytes in the range of values inclusive.  The from and to
     * values can be specified in any order (greater or smaller than each other), but
     * the range returned will always be in order of smallest to highest values.
     * <p>
     * Note: byte values are specified as integer in the range 0 to 255 (unsigned).
     * 
     * @param from The lowest byte value to include.
     * @param to The highest byte value to include.
     * @return byte[] The array of bytes.
     * @throws IllegalArgumentException if the from or to values are not between 0 and 255 inclusive.
     */
    public static byte[] getBytesInRange(final int from, final int to) {
    	ArgUtils.checkIntToByteRange(from, to);
    	final int start = from < to? from : to;
        final int end   = from < to? to : from;
    	final byte[] range = new byte[end-start+1];
        int position = 0;
        for (int value = start; value <= end; value++) {
            range[position++] = (byte) value;
        }
        return range;
    }

    /**
     * Adds all the bytes in a range to a collection of Byte.  The range can be specified
     * either forwards or backwards.
     * 
     * @param from A number in the range from 0 to 255;
     * @param to A number in the range from 0 to 255.
     * @param bytes A set of bytes to add the bytes in the range to.
     * @throws IllegalArgumentException if the from or to values are not between 0 and 255 inclusive
     * 									or the collection of bytes is null.
     */
    public static void addBytesInRange(final int from, final int to, final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	ArgUtils.checkIntToByteRange(from, to);
    	final int start = from < to? from : to;
    	final int end =   from < to? to : from;
    	for (int value = start; value <= end; value++) {
    		bytes.add(Byte.valueOf((byte) value));
    	}
    }

    /**
     * Adds all the bytes matched by a wildmask and a value to a collection of Byte.
     * Each bit in the wildmask which is zero specifies a bit we don't care about (can be zero or one in the value).
     * Each bit in the wildmask which is 1 means that the corresponding bit in the value must be matched.
     * @param wildmask A byte containing a binary bitmask where a zero means we don't care about that bit in the value.
     * @param value    A byte containing a binary value to match.  Only bits whose corresponding bit in the wildmask is 1 make any difference.
     * @param bytes    A collection of bytes to add the bytes to.
     * @param isInverted Whether the results of matching should be inverted (i.e. match all the bytes NOT matched by the wildmask).
     */
    public static void addBytesMatchedByWildBit(final byte wildmask, final byte value,
                                                final Collection<Byte> bytes, final boolean isInverted) {
        final byte valueToMatch = (byte) (value & wildmask);
        final int numToAdd = isInverted? 256 - countBytesMatchingAllBits(wildmask) : countBytesMatchingAllBits(wildmask);
        for (int byteValue = 0, numAdded = 0; numAdded < numToAdd && byteValue < 256; byteValue++) {
            final byte theByte = (byte) byteValue;
            if (((theByte & wildmask) == valueToMatch) ^ isInverted) {
                bytes.add(theByte);
                numAdded++;
            }
        }
    }

    /**
     * Adds all the bytes matched by an ANY wildmask, and a value, to a collection of Byte.
     * Each bit in the wildmask which is zero specifies a bit we don't care about (can be zero or one in the value).
     * Each bit in the wildmask which is one means that at least one corresponding bit in the value must be matched.
     * @param wildmask A byte containing a binary bitmask where a zero means we don't care about that bit in the value.
     * @param value    A byte containing a binary value to match.  Only bits whose corresponding bit in the wildmask is 1 make any difference.
     * @param bytes    A collection of bytes to add the bytes to.
     * @param isInverted Whether the results of matching should be inverted (i.e. match all the bytes NOT matched by the wildmask).

     */
    public static void addBytesMatchedByWildBitAny(final byte wildmask, final byte value,
                                                final Collection<Byte> bytes, final boolean isInverted) {
        final byte valueNotToMatch = (byte) ((~value) & wildmask);
        final int numToAdd = countBytesMatchingAnyBits(wildmask, isInverted);
        for (int byteValue = 0, numAdded = 0; numAdded < numToAdd && byteValue < 256; byteValue++) {
            final byte theByte = (byte) byteValue;
            if (((theByte & wildmask) != valueNotToMatch) ^ isInverted) {
                bytes.add(theByte);
                numAdded++;
            }
        }
    }

   /**
    * Adds all the bytes other than the byte provided to a collection of Byte.  
    * 
    * @param value The byte value which should not appear in the collection of bytes.
    * @param bytes A set of bytes to add all the other bytes to.
    * @throws IllegalArgumentException if collection of bytes passed in is null.
    */
    public static void addInvertedByteValues(final byte value, final Collection<Byte> bytes) {
    	final int intValue = value & 0xFF;
    	if (intValue > 0) {
    		addBytesInRange(0, intValue - 1, bytes);
    	}
    	if (intValue < 255) {
    		addBytesInRange(intValue + 1, 255, bytes);
    	}
    }

    /**
     * Adds all the bytes in an inverted range to a collection.  The inverted range can be specified
     * either forwards or backwards. An inverted range contains all bytes except for the ones
     * specified in the range (which is inclusive).
     * 
     * @param from A number in the range from 0 to 255;
     * @param to A number in the range from 0 to 255.
     * @param bytes A byte collection to add the bytes in the inverted range to.
     * @throws IllegalArgumentException if the from or to values are not between 0 and 255,
     *                                  or the collection of bytes passed in is null.
     */
    public static void addBytesNotInRange(final int from, final int to, final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	ArgUtils.checkIntToByteRange(from, to);
    	final int start = from < to? from : to;
    	final int end =   from < to? to : from;
    	for (int value = 0; value < start; value++) {
    		bytes.add(Byte.valueOf((byte) value));
    	}
    	for (int value = end + 1; value < 256; value++) {
    		bytes.add(Byte.valueOf((byte) value));
    	}
    }
    
    
    /**
     * Returns an inverted set of bytes.  This set of bytes contains all other
     * possible byte values than the ones in the set provided.
     * <p>
     * The set returned is a HashSet with a capacity of the size of the
     * set of bytes passed in, divided by the default load factor.  If you want
     * to specify a different set, call buildInvertedSet instead.
     *  
     * @param bytes A set of bytes.
     * @return A set of all other bytes.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static Set<Byte> invertedSet(final Set<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	final int capacity = (int) (bytes.size() / 0.75);
        final Set<Byte> inverted = new HashSet<Byte>(capacity);
        buildInvertedSet(bytes, inverted);
        return inverted;
    }
    
    
    /**
     * Returns true if one set of bytes is the inverse of the other.
     * Neither set is modified.
     * 
     * @param set The first set to test
     * @param inverseSet The other set to test
     * @return True if the sets are the inverse of each other.
     * @throws IllegalArgumentException if either of the sets passed in is null.
     */
    public static boolean inverseOf(final Set<Byte> set, final Set<Byte> inverseSet) {
    	ArgUtils.checkNullCollection(set, "parameter:set");
    	ArgUtils.checkNullCollection(inverseSet, "parameter:inverseSet");

    	// If the set sizes are not compatible with being the inverse of each other, just return.
    	if (set.size() != 256 - inverseSet.size()) {
            return false;
        }

        // Go through  the bytes in the smaller set, to see if they appear in the
        // bigger set.  If any do, the sets are not inverses of each other.
        final boolean setIsSmaller = set.size() < inverseSet.size();
        final Set<Byte> needles  = setIsSmaller? set : inverseSet;
        final Set<Byte> haystack = setIsSmaller? inverseSet : set;
        for (final Byte needle : needles) {
            if (haystack.contains(needle)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Returns an inverted set of bytes containing all bytes except 
     * for the value passed in.  
     * 
     * @param value The value which should not appear in the set of bytes.
     * @return A set of all other bytes.
     */
    public static Set<Byte> invertedSet(final byte value) {
        final Set<Byte> inverted = new HashSet<Byte>(342);
        for (int i = 0; i < 256; i ++) {
        	inverted.add(Byte.valueOf((byte) i));
        }
        inverted.remove(Byte.valueOf(value));
        return inverted;
    }    
    

    /**
     * Builds an inverted set of bytes.  This set of bytes contains all other
     * possible byte values than the ones in the set provided.
     * 
     * @param bytes A set of bytes.
     * @param invertedSet The set of bytes to add the inverted bytes to.  
     * @throws IllegalArgumentException if either of the sets passed in is null.  
     */
    public static void buildInvertedSet(final Set<Byte> bytes, final Set<Byte> invertedSet) {
    	ArgUtils.checkNullCollection(bytes, "parameter:bytes");
    	ArgUtils.checkNullCollection(invertedSet, "parameter:invertedSet");
    	for (int value = 0; value < 256; value++) {
            if (!bytes.contains((byte) value)) {
                invertedSet.add(Byte.valueOf((byte) value));
            }
        }
    }    
    
    
    /**
     * Removes any bytes in common from the sets passed in (the intersection of the two sets)
     * and returns a list containing the intersection.
     * 
     * @param firstSet The first set of bytes.  Any bytes in common with the second set will be removed.
     * @param secondSet The second set of bytes.  Any bytes in common with the first set will be removed.
     * @return A list containing the intersection of the two sets
     * @throws IllegalArgumentException if either of the sets passed in is null. 
     */
    public static List<Byte> removeIntersection(final Set<Byte> firstSet, final Set<Byte> secondSet) {
    	final List<Byte> bytesRemoved = new ArrayList<Byte>();
        removeIntersection(firstSet, secondSet, bytesRemoved);
        return bytesRemoved;
    }   
    
    
    /**
     * Removes any bytes in common from the sets passed in (the intersection of the two sets)
     * and adds the intersection to a collection also passed in.
     * 
     * @param firstSet The first set of bytes.  Any bytes in common with the second set will be removed.
     * @param secondSet The second set of bytes.  Any bytes in common with the first set will be removed.
     * @param bytesRemoved Any bytes in the intersection of the two sets are added to this collection.
     * @throws IllegalArgumentException if any of the collections passed in are null.
     */
    public static void removeIntersection(final Set<Byte> firstSet, 
                                          final Set<Byte> secondSet,
                                          final Collection<Byte> bytesRemoved) {
    	ArgUtils.checkNullCollection(firstSet, "parameter:firstSet");
    	ArgUtils.checkNullCollection(secondSet, "parameter:secondSet");
    	ArgUtils.checkNullCollection(bytesRemoved, "parameter:bytesRemoved");
    	final Iterator<Byte> byteIterator = firstSet.iterator();
        while (byteIterator.hasNext()) {
            final Byte theByte = byteIterator.next();
            if (secondSet.remove(theByte)) {
                bytesRemoved.add(theByte);
                byteIterator.remove();
            }
        }
    }


    /**
     * Returns a bitmask which contains all the bits in common in the collection of bytes
     * provided - anding all the bytes together.  If the collection passed in is empty,
     * then zero will be returned.
     * 
     * @param bytes A collection of bytes to find the bits in common.
     * @return An integer mask containing only the bits in common, in the range 0 to 255.
     * @throws IllegalArgumentException if the collection of bytes passed in is null.
     */
    public static int getBitsInCommon(final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
        if (bytes.isEmpty()) {
        	return 0;
        }
    	int bitsinCommon = 0xFF;
        for (final Byte b : bytes) {
            bitsinCommon &= b;
        }
        return bitsinCommon;
    }


    /**
     * Calculate a bitmask in which a bit is set if across all the bytes in the 
     * set provided, there were 128 matches for that bit.  This means that the
     * set of bytes contains all the bytes with that bit set.
     * <p>
     * Any given bit can only match a maximum of 128 byte values (the other 128 
     * being the ones where that bit is not set).  
     * 
     * @param bytes A set of bytes 
     * @return int a bitmask containing bits where all possible byte values are
     *             present in the set for that bit.
	 * @throws IllegalArgumentException if the set of bytes passed in is null.             
     */
    public static int getBitsSetForAllPossibleBytes(final Set<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	// Count how many bytes match each bit:
        int bit1 = 0, bit2 = 0, bit3 = 0, bit4 = 0, bit5 = 0, bit6 = 0, bit7 = 0, bit8 = 0;
        for (final Byte b : bytes) {
            final int value = b & 0xFF;
            // Add one to the counter for each bit if the corresponding bit is set.
            bit1 += value & 1;
            bit2 += (value & 2) >> 1;
            bit3 += (value & 4) >> 2;
            bit4 += (value & 8) >> 3;
            bit5 += (value & 16) >> 4;
            bit6 += (value & 32) >> 5;
            bit7 += (value & 64) >> 6;
            bit8 += (value & 128) >> 7;
        }
        // produce a mask of the bits which each matched 128 bytes in the set:
        int anyBitMask = 0;
        if (bit1 == 128) anyBitMask = 1;
        if (bit2 == 128) anyBitMask = anyBitMask | 2;
        if (bit3 == 128) anyBitMask = anyBitMask | 4;
        if (bit4 == 128) anyBitMask = anyBitMask | 8;
        if (bit5 == 128) anyBitMask = anyBitMask | 16;
        if (bit6 == 128) anyBitMask = anyBitMask | 32;
        if (bit7 == 128) anyBitMask = anyBitMask | 64;
        if (bit8 == 128) anyBitMask = anyBitMask | 128;
        return anyBitMask;
    }


    /**
     * Adds the bytes matching an any bitmask (any of the bits can match) to 
     * a collection of bytes.
     * 
     * @param bitMask The bitmask to match.
     * @param bytes The collection of bytes to add the values to.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static void addBytesMatchingAnyBitMask(final byte bitMask,
    											  final Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	// start loop at one - any bitmask matchers can never match the zero byte.
        for (int byteIndex = 1; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & bitMask) != 0) {
                bytes.add(Byte.valueOf((byte) byteIndex));
            }
        }
    }
    
    
    /**
     * Adds the bytes not matching an any bitmask (no bits must match) to
     * a collection of Byte.
     * 
     * @param bitMask The bitmask to not match any bits of.
     * @param bytes The collection of Bytes to add the bytes to.
     * @throws IllegalArgumentException if the set of bytes passed in is null.
     */
    public static void addBytesNotMatchingAnyBitMask(final byte bitMask,
    											     Collection<Byte> bytes) {
    	ArgUtils.checkNullCollection(bytes);
    	for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & bitMask) == 0) {
                bytes.add(Byte.valueOf((byte) byteIndex));
            }
        }
    }
    
    /**
     * Returns the byte represented by a two-digit hex string.
     * 
     * @param hexByte The string containing the 2-digit hex representation of a byte.
     * @return The byte represented by the hexByte.
     * @throws IllegalArgumentException if the string does not contain a valid hex byte or is null.
     */
    public static byte byteFromHex(final String hexByte) {
        if (hexByte != null && hexByte.length() == 2) {
            try {
                return (byte) Integer.parseInt(hexByte, 16);
            } catch (NumberFormatException dropThroughToIllegalArgumentException) {
                // do nothing - illegal argument exception will be thrown below.
            }
        }
        throw new IllegalArgumentException("Not a valid hex byte [" + hexByte + ']');
    }


    /**
     * Returns an integer containing the decimal value (0-255) defined by two hex digits.
     *
     * @param firstHexChar  The first hex byte digit ('0'-'9' 'a'-'f' | 'A' - 'F')
     * @param secondHexChar The second hex byte digit ('0'-'9' 'a'-'f' | 'A' - 'F')
     * @return The decimal value (0-255) defined by the two hex digits, or -1 if not a hex byte.
     */
    public final static int hexByteValue(final char firstHexChar, final char secondHexChar) {
        final int firstHexDigit  = hexDigitValue(firstHexChar);
        final int secondHexDigit = hexDigitValue(secondHexChar);
        if (firstHexDigit < 0 || secondHexDigit < 0) {
            return -1;
        }
        return (firstHexDigit << 4) + secondHexDigit;
    }

    /**
     * Returns an integer containing the decimal value (0-15) defined by a single hex digit.
     *
     * @param digit The hex byte digit ('0'-'9' 'a'-'f' | 'A' - 'F')
     * @return The decimal value (0-15) defined by the hex digit, or -1 if not a hex digit.
     */
    public final static int hexDigitValue(final char digit) {
        if (digit >= '0' && digit <= '9') {
            return digit - '0';
        }
        if (digit >= 'a' && digit <= 'f') {
            return digit - 'a' + 10;
        }
        if (digit >= 'A' && digit <= 'F') {
            return digit - 'A' + 10;
        }
        return -1;
    }

    //TODO: should we have a byte array from hex string method too?  Could be useful, avoid full parser overhead.
    //      byte arrays from hex have to be a fairly common requirement...


    /**
     * Returns a byte value as either a 2-char hex string, or if
     * pretty printing, and the byte value is a printable ASCII
     * character, as a quoted ASCII char, unless it is a single quote
     * character itself, in which case it will still be represented as
     * a hex byte.
     * 
     * @param prettyPrint Whether to pretty print the byte value.
     * @param byteValue The byte value to convert.
     * @return A string containing the byte value as a string.
     */
    public static String byteToString(final boolean prettyPrint, int byteValue) {
        String result;
        if (prettyPrint &&
            byteValue >= START_PRINTABLE_ASCII &&
            byteValue <= END_PRINTABLE_ASCII &&
            byteValue != QUOTE_CHARACTER_VALUE) {
            result = String.format(CHAR_BYTE_FORMAT, byteValue);
        } else {
            result = String.format(HEX_BYTE_FORMAT, byteValue);
        } 
        return result;
    }
    
    
    /**
     * Returns a String containing a 2-digit hex representation of each byte in the
     * array.  If pretty printing and the byte value is a printable ASCII character,
     * these values are returned as a quoted ASCII string delimited with single quotes.
     * Note that a single quote character will be represented as a hex byte
     * Pretty printing also spaces hex bytes. 
     * 
     * @param prettyPrint Whether to pretty print the byte string.
     * @param bytes the array of bytes to convert.
     * @return A string containing the byte values as a string.
     * @throws IllegalArgumentException if the array is null.
     */
    public static String bytesToString(final boolean prettyPrint, final byte[] bytes) {
    	ArgUtils.checkNullByteArray(bytes);
    	return bytesToString(prettyPrint, bytes, 0, bytes.length);
    }
 
    
    /**
     * Returns a String containing a 2-digit hex representation of each byte in the
     * array.  If pretty printing and the byte value is a printable ASCII character,
     * these values are returned as a quoted ASCII string delimited with single quotes.
     * Note that a single quote character will be represented as a hex byte
     * Pretty printing also spaces hex bytes. 
     * 
     * @param prettyPrint Whether to pretty print the byte string.
     * @param bytes the list of Bytes to convert.
     * @return A string containing the byte values as a string.
     * @throws IllegalArgumentException if the collection of bytes is null, or any of the elements are null.
     */
    public static String bytesToString(final boolean prettyPrint, final List<Byte> bytes) {
    	ArgUtils.checkNullCollectionElements(bytes);
    	return bytesToString(prettyPrint, toArray(bytes), 0, bytes.size());
    }

    
    /**
     * Returns a byte array as a String.  If not pretty printed, the bytes
     * are presented as 2 digit hex numbers.  If pretty printed, then bytes
     * which would be printable ASCII characters are represented as such
     * enclosed in single quotes and hex byte elements are spaced.  The single
     * quote character will not be enclosed in single quotes, but will be represented as
     * a hex byte outside of quotes.
     * 
     * @param prettyPrint Whether to pretty print the byte array.
     * @param bytes The bytes to render as a String.
     * @param startIndex the start index to start at, inclusive
     * @param endIndex the end index to stop at, exclusive.
     * @return A string containing a representation of the byte array.
     * @throws IllegalArgumentException if the array is null or the indexes are outside the array bounds.
     */
    public static String bytesToString(final boolean prettyPrint, final byte[] bytes,
                                       final int startIndex, final int endIndex) {
    	ArgUtils.checkBounds(bytes, startIndex, endIndex);
    	final int estimatedSize = prettyPrint? (endIndex - startIndex) * 4 : (endIndex - startIndex) * 2; 
    	final StringBuilder string = new StringBuilder(estimatedSize);
        boolean inString = false;
        boolean firstByte = true;
        for (int byteIndex = startIndex; byteIndex < endIndex; byteIndex++) {
            final int byteValue = 0xFF & bytes[byteIndex];
            if (prettyPrint) {
            	if (!firstByte && !inString) {
            		string.append(' ');
            	}
            	if (byteValue >= START_PRINTABLE_ASCII &&
                    byteValue <= END_PRINTABLE_ASCII &&
                    byteValue != QUOTE_CHARACTER_VALUE) {
                    if (!inString) {
                    	string.append('\'');
                    }
                   	string.append((char) byteValue);
                    inString = true;
            	} else {
            		if (inString) {
            			string.append("' ");
            		}
            		string.append(String.format(HEX_BYTE_FORMAT, byteValue));
                    inString = false;
            	}
            } else {
            	string.append(String.format(HEX_BYTE_FORMAT, byteValue));
            }
            firstByte = false;
        }
        if (prettyPrint && inString) {
            string.append('\'');
        }
        return string.toString();
    }


    /**
     * Returns true if a byte is between ASCII A and Z inclusive.
     * @param theByte The byte to test for ASCII uppercase.
     * @return true if a byte is between ASCII A and Z inclusive.
     */
    public static boolean isUpperCase(final byte theByte) {
        return ((theByte & 0xFF) >= 'A' && (theByte & 0xFF) <= 'Z');
    }

    /**
     * Returns true if the two bytes passed in are a line break and carriage return.
     * @param byte1 A first byte to test
     * @param byte2 A second byte to test
     * @return true if the two bytes passed in are a line break and carriage return.
     */
    public static boolean isLineBreak(final byte byte1, final byte byte2) {
        return ((byte1 == LINE_FEED && byte2 == CARRIAGE_RETURN) ||
                (byte1 == CARRIAGE_RETURN && byte2 == LINE_FEED));
    }

    /**
     * Returns true if a byte is between ASCII a and z inclusive.
     * @param theByte The byte to test for ASCII lowercase.
     * @return true if a byte is between ASCII z and z inclusive.
     */
    public static boolean isLowerCase(final byte theByte) {
        return ((theByte & 0xFF) >= 'a' && (theByte & 0xFF) <= 'z');
    }

    /**
     * Are two bytes a case insensitive ASCII pair - for example 'A' and 'a'.
     * @param byte1 The first byte to test
     * @param byte2 The second byte to test
     * @return true if the two bytes form a case insensitive ASCII pair.
     */
    public static boolean isCaseInsensitive(final byte byte1, final byte byte2) {
        return (isUpperCase(byte1) && byte1 + ASCII_CASE_DIFFERENCE == byte2) ||
               (isUpperCase(byte2) && byte2 + ASCII_CASE_DIFFERENCE == byte1);
    }

}
