package net.byteseek.utils;

/**
 * Created by matt on 09/07/17.
 */
public final class MathUtils {


    /**
     * Returns the log base 2 of an integer, rounded to the floor.
     *
     * Note that the integer must be positive.
     *
     * @param i The integer
     * @return int the log base 2 of an integer, rounded to the floor.
     * @throws IllegalArgumentException if the integer passed in is zero or negative.
     */
    public static int floorLogBaseTwo(final int i) {
    	ArgUtils.checkPositive(i);
        return 31 - Integer.numberOfLeadingZeros(i);
    }

    /**
     * Returns the log base 2 of a long, rounded to the floor.
     * @param i The long
     * @return the log base 2 of the long, rounded to the floor.
     * @throws IllegalArgumentException if the long passed in is zero or negative.
     */
    public static int floorLogBaseTwo(final long i) {
        ArgUtils.checkPositive(i);
        return 63 - Long.numberOfLeadingZeros(i);
    }

    /**
     * Returns the log base 2 of an integer, rounded to the ceiling.
     *
     * Note that the integer must be positive.
     *
     * @param i The integer.
     * @return int the log base 2 of an integer, rounded to the ceiling.
     * @throws IllegalArgumentException if the integer passed in is zero or negative.
     */
    public static int ceilLogBaseTwo(final int i) {
    	ArgUtils.checkPositive(i);
        return 32 - Integer.numberOfLeadingZeros(i - 1);
    }

    /**
     * Returns the log base 2 of an long, rounded to the ceiling.
     *
     * Note that the long must be positive.
     *
     * @param i The long.
     * @return int the log base 2 of an long, rounded to the ceiling.
     * @throws IllegalArgumentException if the long passed in is zero or negative.
     */
    public static int ceilLogBaseTwo(final long i) {
        ArgUtils.checkPositive(i);
        return 64 - Long.numberOfLeadingZeros(i - 1);
    }

    /**
     * Returns true if an integer is a power of two.
     *
     * @param i The integer
     * @return boolean True if the integer was a power of two.
     */
    public static boolean isPowerOfTwo(final int i) {
        return i > 0? (i & (i - 1)) == 0 : false;
    }

    /**
     * Returns a number which is a power of two.  If the number
     * passed in is a power of two, the same number is returned.
     * If the number passed in is not a power of two, then the
     * number returned will be the next highest power of two
     * above it.
     *
     * @param i The number to get the ceiling power of two size.
     * @return The ceiling power of two equal or higher than the number passed in.
     */
    public static int ceilPowerOfTwo(final int i) {
        return 1 << ceilLogBaseTwo(i);
    }

    /**
     * Returns the number which is the next highest power of two bigger than another integer.
     *
     * @param i The integer
     * @return int the closest number which is a power of two and greater than the original integer.
     */
    public static int nextHighestPowerOfTwo(final int i) {
        return Integer.highestOneBit(i) << 1;
    }
}
