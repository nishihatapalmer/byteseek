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
package net.byteseek.searcher.sequence.factory;

import net.byteseek.matcher.bytes.ByteRangeMatcher;
import net.byteseek.matcher.bytes.OneByteMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.bytes.ByteMatcherSearcher;
import net.byteseek.searcher.bytes.ByteSearcher;
import net.byteseek.searcher.sequence.HorspoolSearcher;
import net.byteseek.searcher.sequence.SequenceSearcher;
import net.byteseek.searcher.sequence.ShiftOrUnrolledSearcher;
import net.byteseek.searcher.sequence.analyzer.FullLengthAnalyzer;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class FastSearcherFactoryTest {

    private Random random = new Random();

    /*
     * Test construction
     */

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullShortFactory() {
        new FastSearcherFactory(null,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                FullLengthAnalyzer.ANALYZER, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullWildcardFactory() {
        new FastSearcherFactory(SearcherFactories.SEQUENCEMATCHER_FACTORY,
                null,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                FullLengthAnalyzer.ANALYZER, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullLongFactory() {
        new FastSearcherFactory(SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                null,
                FullLengthAnalyzer.ANALYZER, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullAnalyzer() {
        new FastSearcherFactory(SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructZeroLongSize() {
        new FastSearcherFactory(SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                FullLengthAnalyzer.ANALYZER, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNegativeLongSize() {
        new FastSearcherFactory(SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                SearcherFactories.SEQUENCEMATCHER_FACTORY,
                FullLengthAnalyzer.ANALYZER, -1);
    }

    /*
     * Test selection logic for search algorithms.
     */

    /**
     * Tests that the right searcher is chosen based only on the length of the pattern.
     */
    @Test
    public void testSelectBasedOnLengthOnly() {
        for (int longSize = 1; longSize < 100; longSize++) {
            SequenceSearcherFactory factory = new FastSearcherFactory(
                    SearcherFactories.SHIFTOR_UNROLLED_FACTORY,
                    SearcherFactories.HORSPOOL_FACTORY,
                    SearcherFactories.SEQUENCEMATCHER_FACTORY,
                    FullLengthAnalyzer.ANALYZER, longSize);

            // Test pattern size 1 with a single byte matching:
            SequenceSearcher forwardSearcher = factory.createForwards(OneByteMatcher.valueOf(random.nextInt(255)));
            SequenceSearcher backwardsSearcher = factory.createBackwards(OneByteMatcher.valueOf(random.nextInt(255)));
            assertEquals(ByteSearcher.class, forwardSearcher.getClass());
            assertEquals(ByteSearcher.class, backwardsSearcher.getClass());

            // Test pattern size 1 with multiple bytes matching:
            forwardSearcher = factory.createForwards(new ByteRangeMatcher(32, 127));
            backwardsSearcher = factory.createBackwards(new ByteRangeMatcher(32, 127));
            assertEquals(ByteMatcherSearcher.class, forwardSearcher.getClass());
            assertEquals(ByteMatcherSearcher.class, backwardsSearcher.getClass());

            // Test patterns greater than 2:
            for (int patternSize = 2; patternSize < 101; patternSize++) {
                Class expectedValue = patternSize < longSize ? ShiftOrUnrolledSearcher.class : HorspoolSearcher.class;
                SequenceMatcher pattern = OneByteMatcher.valueOf('A').repeat(patternSize);
                forwardSearcher = factory.createForwards(pattern);
                assertEquals(expectedValue, forwardSearcher.getClass());
                backwardsSearcher = factory.createBackwards(pattern);
                assertEquals(expectedValue, backwardsSearcher.getClass());
            }
        }
    }

    /**
     * Tests that the wildcard searcher will be selected if there is no best subsequence available according to the long analyzer.
     */
    @Test
    public void testSelectWildcardSearcherIfNoBestSubSequence() {

    }

    /**
     * Tests that it will select a subsequence to search for if there are wildcard patterns that prevent using the long searcher.
     * It will choose the short searcher if the subsequence is short, and the long searcher if it's longer.
     */
    @Test
    public void testSelectSubSequenceSearcherWithBestSubSequence() {

    }

}