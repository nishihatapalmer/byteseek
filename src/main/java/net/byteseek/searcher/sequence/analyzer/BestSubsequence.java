package net.byteseek.searcher.sequence.analyzer;

public class BestSubsequence {
    public final int startPos;
    public final int endPos;
    public BestSubsequence(int startPos, int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
    public int length() {
        return endPos - startPos + 1;
    }
}