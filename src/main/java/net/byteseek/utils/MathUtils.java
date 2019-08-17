/*
 * Copyright Matt Palmer 2009-19, All rights reserved.
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
    	ArgUtils.checkGreaterThanZero(i);
        return 31 - Integer.numberOfLeadingZeros(i);
    }

    /**
     * Returns the log base 2 of a long, rounded to the floor.
     * @param i The long
     * @return the log base 2 of the long, rounded to the floor.
     * @throws IllegalArgumentException if the long passed in is zero or negative.
     */
    public static int floorLogBaseTwo(final long i) {
        ArgUtils.checkGreaterThanZero(i);
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
    	ArgUtils.checkGreaterThanZero(i);
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
        ArgUtils.checkGreaterThanZero(i);
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
