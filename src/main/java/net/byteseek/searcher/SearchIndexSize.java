/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.searcher;

import net.byteseek.utils.ArgUtils;

/**
 * An enumeration of possible search index sizes as powers of two, along with
 * the method an algorithm should use to select the final size.
 * Note that sizes relate to the number of elements in a search index, not the amount of memory used.
 * Byteseek search table indexes are usually arrays of integers or longs.
 * <p>
 * The methods a search algorithm can use to select a search index size are:
 * EXACTLY: the search algorithm should use exactly the size specified.
 * MAX:     the search algorithm should select the best size up to a maximum of the size specified.
 * <p>
 * The best search is usually obtained using the most appropriate table size - not too small or too large.
 * If a table is too small, search performance suffers due to too many collisions in the index.
 * If a table is too big no search performance is gained, but it suffers due to cache misses in memory.
 * It may be possible to obtain faster performance by specifying a larger table size using EXACTLY,
 * but in general using MAX will give reasonable performance no matter the pattern complexity.
 * With MAX the algorithm must use heuristics (supported by performance profiling and/or theory) to select the most appropriate size.
 * Selecting an exact size should be supported by some evidence that it benefits the type of searching being done.
 *
 * It is also useful to specify exact sizes when performance testing an algorithm that supports different index sizes.
 *
 * Created by matt on 03/06/17.
 */
public enum SearchIndexSize {

    MAX_2(    1, Method.MAX), EXACTLY_2(    1, Method.EXACTLY),
    MAX_4(    2, Method.MAX), EXACTLY_4(    2, Method.EXACTLY),
    MAX_8(    3, Method.MAX), EXACTLY_8 (   3, Method.EXACTLY),
    MAX_16(   4, Method.MAX), EXACTLY_16(   4, Method.EXACTLY),
    MAX_32(   5, Method.MAX), EXACTLY_32(   5, Method.EXACTLY),
    MAX_64(   6, Method.MAX), EXACTLY_64(   6, Method.EXACTLY),
    MAX_128(  7, Method.MAX), EXACTLY_128(  7, Method.EXACTLY),
    MAX_256(  8, Method.MAX), EXACTLY_256(  8, Method.EXACTLY),
    MAX_512(  9, Method.MAX), EXACTLY_512(  9, Method.EXACTLY),
    MAX_1K(  10, Method.MAX), EXACTLY_1K(  10, Method.EXACTLY),
    MAX_2K(  11, Method.MAX), EXACTLY_2K(  11, Method.EXACTLY),
    MAX_4K(  12, Method.MAX), EXACTLY_4K(  12, Method.EXACTLY),
    MAX_8K(  13, Method.MAX), EXACTLY_8K(  13, Method.EXACTLY),
    MAX_16K( 14, Method.MAX), EXACTLY_16K( 14, Method.EXACTLY),
    MAX_32K( 15, Method.MAX), EXACTLY_32K( 15, Method.EXACTLY),
    MAX_64K( 16, Method.MAX), EXACTLY_64K( 16, Method.EXACTLY),
    MAX_128K(17, Method.MAX), EXACTLY_128K(17, Method.EXACTLY),
    MAX_256K(18, Method.MAX), EXACTLY_256K(18, Method.EXACTLY),
    MAX_512K(19, Method.MAX), EXACTLY_512K(19, Method.EXACTLY),
    MAX_1M(  20, Method.MAX), EXACTLY_1M(  20, Method.EXACTLY),
    MAX_2M(  21, Method.MAX), EXACTLY_2M(  21, Method.EXACTLY),
    MAX_4M(  22, Method.MAX), EXACTLY_4M(  22, Method.EXACTLY),
    MAX_8M(  23, Method.MAX), EXACTLY_8M(  23, Method.EXACTLY),
    MAX_16M( 24, Method.MAX), EXACTLY_16M( 24, Method.EXACTLY);

    /**
     * Returns a SearchIndexSize given a SearchIndex Method and a power of two size.
     *
     * @param method       The method to derive the search index size - up to a value, or exactly a value.
     * @param powerTwoSize The size of the search index size expressed as a power of two between 1 and 24.
     * @return A SearchIndexSize enum for the method and power two size specified.
     * @throws IllegalArgumentException if the method is null, or the powerTwoSize is not between 1 and 24 inclusive.
     */
    public static SearchIndexSize getIndexSize(final Method method, final int powerTwoSize) {
        ArgUtils.checkNullObject(method, "method");
        switch (powerTwoSize) {
            case 1:  return method == Method.MAX? MAX_2    : EXACTLY_2;
            case 2:  return method == Method.MAX? MAX_4    : EXACTLY_4;
            case 3:  return method == Method.MAX? MAX_8    : EXACTLY_8;
            case 4:  return method == Method.MAX? MAX_16   : EXACTLY_16;
            case 5:  return method == Method.MAX? MAX_32   : EXACTLY_32;
            case 6:  return method == Method.MAX? MAX_64   : EXACTLY_64;
            case 7:  return method == Method.MAX? MAX_128  : EXACTLY_128;
            case 8:  return method == Method.MAX? MAX_256  : EXACTLY_256;
            case 9:  return method == Method.MAX? MAX_512  : EXACTLY_512;
            case 10: return method == Method.MAX? MAX_1K   : EXACTLY_1K;
            case 11: return method == Method.MAX? MAX_2K   : EXACTLY_2K;
            case 12: return method == Method.MAX? MAX_4K   : EXACTLY_4K;
            case 13: return method == Method.MAX? MAX_8K   : EXACTLY_8K;
            case 14: return method == Method.MAX? MAX_16K  : EXACTLY_16K;
            case 15: return method == Method.MAX? MAX_32K  : EXACTLY_32K;
            case 16: return method == Method.MAX? MAX_64K  : EXACTLY_64K;
            case 17: return method == Method.MAX? MAX_128K : EXACTLY_128K;
            case 18: return method == Method.MAX? MAX_256K : EXACTLY_256K;
            case 19: return method == Method.MAX? MAX_512K : EXACTLY_512K;
            case 20: return method == Method.MAX? MAX_1M   : EXACTLY_1M;
            case 21: return method == Method.MAX? MAX_2M   : EXACTLY_2M;
            case 22: return method == Method.MAX? MAX_4M   : EXACTLY_4M;
            case 23: return method == Method.MAX? MAX_8M   : EXACTLY_8M;
            case 24: return method == Method.MAX? MAX_16M  : EXACTLY_16M;
            default: throw new IllegalArgumentException("Power of two size must be between 1 and 24, inclusive.");
        }
    }

    /**
     * A size expressed as a power of two.
     */
    private final int powerTwoSize;

    /**
     * The method a search algorithm should use to select the search index size.
     */
    private final Method sizeMethod;

    /**
     * Constructor for SearchIndexSize.
     *
     * @param powerTwoSize The search index size specified as a power of two.
     * @param sizeChoice   The method a search algorithm should use to select the search index size.
     */
    SearchIndexSize(final int powerTwoSize, final Method sizeChoice) {
        this.powerTwoSize = powerTwoSize;
        this.sizeMethod = sizeChoice;
    }

    /**
     * An enumeration of methods search algorithms should use to select a size for its search index.
     */
    public enum Method {
        /**
         * The search algorithm should select the best size, up to a maximum specified size.
         * This will normally give good or reasonable performance.
         */
        MAX,

        /**
         * The search algorithm should select exactly a specified size for the search index.
         * This may or may not give good performance, depending on the pattern complexity and the size chosen.
         */
        EXACTLY
    }

    /**
     * Returns the method a search algorithm should use to select its search index size.
     * @return the method a search algorithm should use to select its search index size.
     */
    public Method getSizeMethod() {
        return sizeMethod;
    }

    /**
     * Returns the search index size as a power of two.
     * @return the search index size as a power of two.
     */
    public int getPowerTwoSize() {
        return powerTwoSize;
    }

    /**
     * Returns the search index size.
     * @return the search index size.
     */
    public int getIndexSize() {
        return 1 << powerTwoSize;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getIndexSize() + "," + sizeMethod.name() + ")";
    }
}
