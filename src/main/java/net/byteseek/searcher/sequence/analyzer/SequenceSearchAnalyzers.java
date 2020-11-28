/*
 * Copyright Matt Palmer 2020, All rights reserved.
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
package net.byteseek.searcher.sequence.analyzer;

/**
 * A collection of static SequenceSearchAnalyzers for each search algorithm in byteseek.
 */
public final class SequenceSearchAnalyzers {

    /*
     * Searchers which only search a single byte position at a time.
     */
    public static final SequenceSearchAnalyzer BYTE_ANALYZER = LengthOneSearchAnalyzer.ANALYZER;
    public static final SequenceSearchAnalyzer BYTE_MATCHER_ANALYZER = LengthOneSearchAnalyzer.ANALYZER;

    /*
     * Searchers which always search for the entire sequence, subsequences don't help.
     */
    public static final SequenceSearchAnalyzer SEQUENCE_MATCHER_ANALYZER = FullLengthAnalyzer.ANALYZER;
    public static final SequenceSearchAnalyzer SHIFT_OR_ANALYZER = FullLengthAnalyzer.ANALYZER;
    public static final SequenceSearchAnalyzer SHIFT_OR_UNROLLED_ANALYZER = FullLengthAnalyzer.ANALYZER;

    /*
     * Searchers whose most efficient subsequence depends on the number of bytes matching in each position,
     * and which can be extended away from the direction of search into higher numbers of bytes without
     * losing performance, and possibly improving it.
     */
    public static final SequenceSearchAnalyzer HORSPOOL_ANALYZER = NumBytesAnalyzer.ANALYZER_EXTEND;
    public static final SequenceSearchAnalyzer HORSPOOL_UNROLLED_ANALYZER = NumBytesAnalyzer.ANALYZER_EXTEND;
    public static final SequenceSearchAnalyzer SIGNED_HORSPOOL_ANALYZER = NumBytesAnalyzer.ANALYZER_EXTEND;
    public static final SequenceSearchAnalyzer SUNDAY_ANALYZER = NumBytesAnalyzer.ANALYZER_EXTEND;

    /*
     * Searchers whose most efficient subsequence depends on the number of permutations of bytes in the qGrams,
     * but which can be extended a bit into higher permutation qgrams once a good subsequence is found,
     * in the opposite direction of search.
     */
    public static final SequenceSearchAnalyzer SIGNED_HASH2_ANALYZER = BytePermutationAnalyzer.ANALYZER2_EXTEND;
    public static final SequenceSearchAnalyzer SIGNED_HASH3_ANALYZER = BytePermutationAnalyzer.ANALYZER3_EXTEND;

    /*
     * Searchers whose most efficient subsequence depends on the number of permutations of bytes in the qGrams,
     * and which should not be extended into higher density of permutations anywhere (e.g. uses a bloom filter like
     * approach, so don't want to fill up the table).
     */
    public static final SequenceSearchAnalyzer QGRAM_FILTER2_ANALYZER = BytePermutationAnalyzer.ANALYZER2;
    public static final SequenceSearchAnalyzer QGRAM_FILTER3_ANALYZER = BytePermutationAnalyzer.ANALYZER3;

    /**
     * Private constructor.
     */
    private SequenceSearchAnalyzers() {
    }

}
