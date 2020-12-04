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
 * Represents the start and end position of a sequence which forms the best subsequence to search for.
 */
public final class BestSubsequence {

    private final int startPos;
    private final int endPos;

    /**
     * Constructs a BestSubsequence given a start and end position.
     *
     * @param startPos The start position of the best subsequence.
     * @param endPos The end position of the best subsequence.
     */
    public BestSubsequence(final int startPos, final int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    /**
     * @return the length of the subsequence.
     */
    public int length() {
        return endPos - startPos + 1;
    }

    /**
     * @return the start position of the subsequence.
     */
    public int getStartPos() {
        return startPos;
    }

    /**
     * @return the end position of the subsequence.
     */
    public int getEndPos() {
        return endPos;
    }

    @Override
    public int hashCode() {
        return (31 + startPos) * (7 + endPos);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BestSubsequence)) {
            return false;
        }
        final BestSubsequence other = (BestSubsequence) obj;
        return startPos == other.startPos && endPos == other.endPos;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + startPos + ',' + endPos + ')';
    }
}