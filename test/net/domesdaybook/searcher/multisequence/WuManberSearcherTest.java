/*
 * Copyright Matt Palmer 2012, All rights reserved.
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
package net.domesdaybook.searcher.multisequence;

import java.util.List;
import net.domesdaybook.reader.Reader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matt Palmer
 */
public class WuManberSearcherTest {
    
    public WuManberSearcherTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getWuManberRecommendedBlockSize method, of class WuManberSearcher.
     */
    @Test
    public void testGetWuManberRecommendedBlockSize() {
        System.out.println("getWuManberRecommendedBlockSize");
        /* test what block sizes we get:
        for (int numSeqs = 1; numSeqs < 1000; numSeqs += 25) {
            for (int minLength = 1; minLength < 100; minLength += 5) {
                final int blockSize = getBlockSize(minLength, numSeqs);
                String message = String.format("Num seqs:%d\tMin length:%d\tBlocksize:%d", numSeqs, minLength, blockSize);
                System.out.println(message);
            }
        }
         * 
         */
        int i = 33;
        int j = i | 0x80000000;
        int k = j & 0x0FFFFFFF;
        System.out.println(String.format("i:%d j:%d k:%d", i, j, k));
    }
    
    private int getBlockSize(final int minimumLength, final int numberOfSequences) {
        final double optimumBlockSize = WuManberSearcher.getWuManberRecommendedBlockSize(256, minimumLength, numberOfSequences);
        final int possibleBlockSize = (int) Math.ceil(optimumBlockSize);
        final int notGreaterThanMinimumLength = minimumLength < possibleBlockSize?
                                                minimumLength : possibleBlockSize;
        return notGreaterThanMinimumLength > 1 ? notGreaterThanMinimumLength : 1;
    }    


}
