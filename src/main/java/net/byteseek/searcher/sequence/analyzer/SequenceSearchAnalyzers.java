package net.byteseek.searcher.sequence.analyzer;

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
     * Searchers whose most efficient subsequence depends on the number of bytes matching in each position
     */
    public static final SequenceSearchAnalyzer HORSPOOL_ANALYZER = NumBytesAnalyzer.ANALYZER;
    public static final SequenceSearchAnalyzer HORSPOOL_UNROLLED_ANALYZER = NumBytesAnalyzer.ANALYZER;
    public static final SequenceSearchAnalyzer SIGNED_HORSPOOL_ANALYZER = NumBytesAnalyzer.ANALYZER;
    public static final SequenceSearchAnalyzer SUNDAY_ANALYZER = NumBytesAnalyzer.ANALYZER;

    /*
     * Searchers whose most efficient subsequence depends on the number of permutations of bytes in a qGram
     */
    public static final SequenceSearchAnalyzer SIGNED_HASH2_ANALYZER = BytePermutationAnalyzer.ANALYZER2_EXTEND;
    public static final SequenceSearchAnalyzer SIGNED_HASH3_ANALYZER = BytePermutationAnalyzer.ANALYZER3_EXTEND;
    public static final SequenceSearchAnalyzer QGRAM_FILTER2_ANALYZER = BytePermutationAnalyzer.ANALYZER2;
    public static final SequenceSearchAnalyzer QGRAM_FILTER3_ANALYZER = BytePermutationAnalyzer.ANALYZER3;

}
