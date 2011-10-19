/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
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
 * 
 */

package net.domesdaybook.bytes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
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
 * Note: This class will probably be split up into smaller and more
 * targeted utility classes in future.
 * 
 * @author Matt Palmer
 */
public final class ByteUtilities {

    private static final int QUOTE_CHARACTER_VALUE = 39;
    private static final int START_PRINTABLE_ASCII = 32;
    private static final int END_PRINTABLE_ASCII = 126;

    private static int[] VALID_ALL_BITMASK_SET_SIZES = {1, 2, 4, 8, 16, 32, 64, 128, 256};
    private static int[] VALID_ANY_BITMASK_SET_SIZES = {0, 128, 192, 224, 240, 248, 252, 254, 255};

    /**
     * Private constructor for static utility class.
     */
    private ByteUtilities() {
    }


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
        int bits = (int) b;
        int result = bits - ((bits >>> 1) & 0x55);
        result = ((result >>> 2) & 0x33) + (result & 0x33);
        result = ((result >>> 4) + result) & 0x0F;
        return result;
    }


    /**
     * Returns the number of unset bits in a given byte.
     *
     * @param b The byte to count the unset bits.
     * @return The number of bits unset in the byte.
     */
    public static int countUnsetBits(byte b) {
        return 8 - countSetBits(b);
    }


    /**
     * Returns the number of bytes which would match all the bits
     * in a given bitmask.
     *
     * @param bitmask The bitmask.
     * @return The number of bytes matching all the bits in the bitmask.
     */
    public static int countBytesMatchingAllBits(final byte bitmask) {
        // 00000000 - 1 << 8 = 256
        // 00000001 - 1 << 7 = 128
        // 00000011 - 1 << 6 = 64
        // 00000111 - 1 << 5 = 32
        // 00001111 - 1 << 4 = 16
        // 00011111 - 1 << 3 = 8
        // 00111111 - 1 << 2 = 4
        // 01111111 - 1 << 1 = 2
        // 11111111 - 1 << 0 = 1
        // which particular bits are set or unset does not affect the calculation.
        return 1 << countUnsetBits(bitmask);
    }

    /**
     * Returns the number of bytes which would match any of the bits
     * in a given bitmask.
     * 
     * @param bitmask The bitmask.
     * @return The number of bytes matching any of the bits in the bitmask.
     */
    public static int countBytesMatchingAnyBit(final byte bitmask) {
        // 00000000 - 256 - 256 = 0    (no match: zero).
        // 00000001 - 256 - 128 = 128  (no match: half the bytes where that bit is not set)
        // 00000011 - 256 - 64  = 192  (no match: zero & 63 other possible values)
        // 00000111 - 256 - 32  = 224  (no match: zero & 31 other possible values)
        // 00001111 - 256 - 16  = 240  (no match: zero & 15 other possible values)
        // 00011111 - 256 - 8   = 248  (no match: zero & 7 other possible values)
        // 00111111 - 256 - 4   = 252  (no match: zero, 10000000, 11000000, 01000000)
        // 01111111 - 256 - 2   = 254  (no match: zero and 10000000)
        // 11111111 - 256 - 1   = 255  (no match: zero)
        // which particular bits are set or unset does not affect the calculation.
        return 256 - countBytesMatchingAllBits(bitmask);
    }



    /**
     * Returns a list of bytes which would match all the bits in a given bitmask.
     *
     * @param bitMask The bitmask to match.
     * @return A list of bytes matching the bitmask.
     */
    public static List<Byte> getBytesMatchingAllBitMask(final byte bitMask) {
        final List<Byte> bytes = new ArrayList<Byte>(128);
        for (int byteIndex = 0; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & bitMask) == bitMask) {
                bytes.add((byte) byteIndex);
            }
        }
        return bytes;
    }


    public static Byte getAllBitMaskForBytes(final byte[] bytes) {
        return getAllBitMaskForBytes(toSet(bytes));
    }


    public static Set<Byte> toSet(final byte[] bytes) {
        final Set<Byte> setOfBytes = new LinkedHashSet<Byte>((int) Math.round(bytes.length * 1.25));
        addAll(bytes, setOfBytes);
        return setOfBytes;
    }

    
    public static void addAll(final byte[] bytes, final Collection<Byte> toCollection) {
        final int size = bytes.length;
        for (int count = 0; count < size; count++) {
            toCollection.add(bytes[count]);
        }
    }
    
    
    public static byte[] toArray(final Collection<Byte> collection) {
        final byte[] result = new byte[collection.size()];
        int position = 0;
        for (final Byte b : collection) {
            result[position++] = b;
        }
        return result;
    }
    
    
    public static byte[] reverseArray(final byte[] array) {
        final int lastpos = array.length - 1;
        final byte[] reversed = new byte[array.length];
        for (int i = 0; i <= lastpos; i++) {
            reversed[i] = array[lastpos - i];
        }
        return reversed;
    }
    
    
    public static int[] toIntArray(final byte[] bytes) {
        final int[] integers = new int[bytes.length];
        for (int index = 0; index < bytes.length; index++) {
            integers[index] = (int) bytes[index] & 0xFF;
        }
        return integers;
    }


    public static byte[] getAllByteValues() {
        return getBytesInRange(0, 255);
    }


    public static byte[] getBytesInRange(final int from, final int to) {
        final byte[] range = new byte[to-from+1];
        int position = 0;
        for (int value = from; value <= to; value++) {
            range[position++] = (byte) value;
        }
        return range;
    }
    
    
    public static Set<Byte> invertedSet(final Set<Byte> bytes) {
        Set<Byte> invertedSet = new LinkedHashSet<Byte>(320);
        for (int value = 0; value < 256; value++) {
            if (!bytes.contains((byte) value)) {
                invertedSet.add((byte) value);
            }
        }
        return invertedSet;
    }

    
    /**
     *
     * @param bytes A set of bytes to find an all bitmask to match.
     * @return A bitmask to match the set with, or null if no bitmask exists for that set of bytes.
     */
    public static Byte getAllBitMaskForBytes(final Set<Byte> bytes) {
        Byte allBitMask = null;
        final int setSize = bytes.size();
        if (setSize == 256) { // if we have all byte values, then a bitmask of zero matches all of them.
            allBitMask = new Byte((byte) 0);
        } else if (Arrays.binarySearch(VALID_ALL_BITMASK_SET_SIZES, setSize) >= 0) {
            // Build a candidate bitmask from the bits all the bytes have in common.
            int bitsInCommon = getBitsInCommon(bytes);
            if (bitsInCommon > 0) {
                // If the number of bytes in the set is the same as the number of bytes
                // which would match the bitmask, then the set of bytes can be matched
                // by that bitmask.
                final byte mask = (byte) bitsInCommon;
                if (setSize == countBytesMatchingAllBits(mask)) {
                    allBitMask = Byte.valueOf(mask);
                }
            }
        }
        return allBitMask;
    }



    /**
     *
     * @param bytes A set of bytes to find an any bitmask to match.
     * @return A bitmask to match the set with, or null if no bitmask exists for that set of bytes.
     */
    public static Byte getAnyBitMaskForBytes(final Set<Byte> bytes) {
        Byte anyBitMask = null;
        final int setSize = bytes.size();
        if (setSize == 0) {
            anyBitMask = new Byte((byte) 0);
        } else if (Arrays.binarySearch(VALID_ANY_BITMASK_SET_SIZES, setSize) >= 0) {
            // Find which bits in the set are matched by 128 bytes in the set.
            // These bits might form a valid any bitmask.
            int possibleAnyMask = getBitsSetForAllPossibleBytes(bytes);

            // Check that the any bitmask produced gives a set of bytes
            // the same size as the set provided.
            if (possibleAnyMask > 0) {
                final byte mask = (byte) possibleAnyMask;
                if (setSize == countBytesMatchingAnyBit(mask)) {
                    anyBitMask = Byte.valueOf(mask);
                }
            }
        }
        return anyBitMask;
    }


    public static Byte getAnyBitMaskForBytes(final byte[] bytes) {
        return getAnyBitMaskForBytes(toSet(bytes));
    }
    
    /**
     *
     * @param bytes A set of bytes to find the bits in common.
     * @return An integer mask containing only the bits in common.
     */
    public static int getBitsInCommon(final Set<Byte> bytes) {
        int bitsinCommon = 0xFF;
        for (final Byte b : bytes) {
            bitsinCommon &= b;
        }
        return bitsinCommon;
    }


    public static int getBitsSetForAllPossibleBytes(final Set<Byte> bytes) {
        // Count how many bytes match each bit:
        int bit1 = 0, bit2 = 0, bit3 = 0, bit4 = 0, bit5 = 0, bit6 = 0, bit7 = 0, bit8 = 0;
        for (Byte b : bytes) {
            final int value = b & 0xFF;
            bit1 += value & 1;
            bit2 += (value & 2) >> 1;
            bit3 += (value & 4) >> 2;
            bit4 += (value & 8) >> 3;
            bit5 += (value & 16) >> 4;
            bit6 += (value & 32) >> 5;
            bit7 += (value & 64) >> 6;
            bit8 += (value & 128) >> 7;
            /*
            if ((value & 1) > 0) bit1 += 1;
            if ((value & 2) > 0) bit2 += 1;
            if ((value & 4) > 0) bit3 += 1;
            if ((value & 8) > 0) bit4 += 1;
            if ((value & 16) > 0) bit5 += 1;
            if ((value & 32) > 0) bit6 += 1;
            if ((value & 64) > 0) bit7 += 1;
            if ((value & 128) > 0) bit8 += 1;
            */
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
     *
     * @param bytes The set of bytes to find all the bits used in.
     * @return A bitmask containing all the bits used across the set of bytes.
     */
    public static int getAllBitsUsed(final Set<Byte> bytes) {
        int bitsUsed = 0x00;
        for (final Byte b : bytes) {
            bitsUsed |= b;
        }
        return bitsUsed;
    }


    /**
     *
     * @param bytes A set of bytes to test for unused bits.
     * @return A bitmask containing all the bits which were unused across the set of bytes.
     */
    public static int getUnusedBits(final Set<Byte> bytes) {
        return getAllBitsUsed(bytes) ^ 0xFF;
    }



    /**
     * Returns a list of bytes which would match any of the bits in a given bitmask.
     * @param bitMask The bitmask to match.
     * @return A list of all the bytes matching the any bitmask.
     */
    public static List<Byte> getBytesMatchingAnyBitMask(final byte bitMask) {
        final List<Byte> bytes = new ArrayList<Byte>(256);
        // start loop at one - any bitmask matchers can never match the zero byte.
        for (int byteIndex = 1; byteIndex < 256; byteIndex++) {
            final byte byteValue = (byte) byteIndex;
            if ((byteValue & bitMask) != 0) {
                bytes.add((byte) byteIndex);
            }
        }
        return bytes;
    }

    
    /**
     * Returns the byte represented by a two-digit hex string.
     * 
     * @param hexByte The string containing the 2-digit hex representation of a byte.
     * @return The byte represented by the hexByte.
     * @throws IllegalArgumentException if the string does not contain a valid hex byte.
     */
    public static byte byteFromHex(final String hexByte) {
        if (hexByte != null && hexByte.length() == 2) {
            try {
                return Byte.valueOf(hexByte, 16);
            } catch (NumberFormatException ex) {
                // do nothing - illegal argument exception will be thrown below.
            }
        }
        throw new IllegalArgumentException("Not a valid hex byte.");
    }

    
    /**
     * Returns a byte value as either a 2-char hex string, or if
     * pretty printing, and the byte value is a printable ASCII
     * character, as a quoted ASCII char, unless it is a single quote
     * character itself, in which case it will still be represented as
     * a hex byte.
     * 
     * @param prettyPrint Whether to pretty print the byte value.
     * @param value The byte value to represent as a string.
     * @return A string containing the byte value as a string.
     */
    public static String byteToString(final boolean prettyPrint, int byteValue) {
        String result;
        if (prettyPrint) {
            if (byteValue >= START_PRINTABLE_ASCII &&
                byteValue <= END_PRINTABLE_ASCII &&
                byteValue != QUOTE_CHARACTER_VALUE) {
                result = String.format(" '%c' ", byteValue);
            } else {
                result = String.format(" %02x ", byteValue);
            }
        } else {
            result = String.format("%02x", byteValue);
        }
        return result;
    }
    
    /**
     * Returns a byte array as a String.  If not pretty printed, the bytes
     * are presented as 2 digit hex numbers.  If pretty printed, then bytes
     * which would be printable ASCII characters are represented as such
     * enclosed in single quotes.
     * 
     * @param prettyPrint Whether to pretty print the byte array.
     * @param bytes The bytes to render as a String.
     * @return A string containing a representation of the byte array.
     */
    public static String bytesToString(final boolean prettyPrint, final byte[] bytes) {
        final StringBuilder hexString = new StringBuilder();
        boolean inString = false;
        for (int byteIndex = 0, byteLength = bytes.length;
            byteIndex < byteLength; byteIndex++) {
            final int byteValue = 0xFF & bytes[byteIndex];
            if (prettyPrint &&
                    byteValue >= START_PRINTABLE_ASCII &&
                    byteValue <= END_PRINTABLE_ASCII &&
                    byteValue != QUOTE_CHARACTER_VALUE) {
                final String formatString = inString ? "%c" : " '%c";
                hexString.append(String.format(formatString, (char) byteValue));
                inString = true;
            } else {
                final String formatString = prettyPrint? inString? "' %02x" : "%02x" : "%02x";
                hexString.append(String.format(formatString, byteValue));
                inString = false;
            }
        }
        if (prettyPrint && inString) {
            hexString.append("' ");
        }
        return hexString.toString();
    }    
    
}
