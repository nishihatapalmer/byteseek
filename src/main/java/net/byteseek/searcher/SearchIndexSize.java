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
    MAX_1M(  20, Method.MAX), EXACTLY_1M(  20, Method.EXACTLY);

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
