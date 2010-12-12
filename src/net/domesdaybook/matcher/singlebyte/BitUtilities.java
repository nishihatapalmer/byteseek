/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

/**
 *
 * @author matt
 */
public class BitUtilities {

    private static int[] MASK = {0x55, 0x33, 0x0F};

    private BitUtilities() {
    }

    // algorithm taken from:
    // http://www-graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
    public static int countSetBits(byte b) {
        int bits = (int) b;
        int result = bits - ((bits >>> 1) & MASK[0]);
        result = ((result >>> 2) & MASK[1]) + (result & MASK[1]);
        result = ((result >>> 4) + result) & MASK[2];
        return result;
    }

    public static int countUnsetBits(byte b) {
        return 8 - countSetBits(b);
    }

    public static int countBytesMatchingAllBits(byte bitmask) {
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

    public static int countBytesMatchingAnyBit(byte bitmask) {
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
    
}
