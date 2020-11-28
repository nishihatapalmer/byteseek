/*
 * Copyright Matt Palmer 2017-19, All rights reserved.
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

import net.byteseek.io.reader.InputStreamReader;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * General tests for all searchers.
 *
 * Created by matt on 05/05/17.
 */

//TODO: tests for byte classes, not just byte strings...

public class GeneralSearcherTests extends SearchersToTest {

    // Tests for searching empty data:

    @Test
    public void testSearchForwardsEmptyByteArray() {
        createSearchers("x", false);
        byte[] data = new byte[0];
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchForwards(data);
                assertTrue(results.isEmpty());

                //int result = searcher.searchSequenceForwards(data);
                //assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsEmptyByteArray() {
        createSearchers("x", false);
        byte[] data = new byte[0];
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchBackwards(data);
                assertTrue(results.isEmpty());
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }

    @Test
    public void testSearchForwardsEmptyReader() {
        createSearchers("x", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream(new byte[0]));
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchForwards(data);
                assertTrue(results.isEmpty());
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsEmptyReader() {
        createSearchers("x", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream(new byte[0]));
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchBackwards(data);
                assertTrue(results.isEmpty());
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex);
            }
        }
    }

    // Tests where pattern is longer than the data.

    @Test
    public void testSearchForwardsShortByteArray() {
        createSearchers("xyz123abc", false);
        byte[] data = new byte[2];
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchForwards(data);
                assertTrue(searcher.toString(), results.isEmpty());
            } catch (Exception ex) {
                //fail("Searcher " + searcher + " had exception " + ex.getCause());
                List<MatchResult> results = searcher.searchForwards(data);
            }
        }
    }


    @Test
    public void testSearchBackwardsShortByteArray() {
        createSearchers("xyz123abc", false);
        byte[] data = new byte[2];
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchBackwards(data);
                assertTrue(results.isEmpty());
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }

    @Test
    public void testSearchForwardsShortReader() {
        createSearchers("xyz123abc", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream(new byte[2]));
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchForwards(data);
                assertTrue(results.isEmpty());
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsShortReader() {
        createSearchers("xyz123abc", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream(new byte[2]));
        for (Searcher searcher : searchers) {
            try {
                List<MatchResult> results = searcher.searchBackwards(data);
                assertTrue(results.isEmpty());
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex);
            }
        }
    }

    // Boundary tests around the start of data.

    @Test
    public void testSearchBackwardsArrayAroundZero() {
        createSearchers("xyz", false);
        byte[] data = "xyz".getBytes();
        for (Searcher searcher : searchers) {
            List<MatchResult> results = searcher.searchBackwards(data, -1);
            if (!results.isEmpty()) {
                results = searcher.searchBackwards(data, -1);
            }
            assertTrue(searcher.toString(), results.isEmpty());

            results = searcher.searchBackwards(data, 0);
            assertEquals(searcher.toString(), 1, results.size());
            assertEquals(searcher.toString(), 0, results.get(0).getMatchPosition());
        }
    }

    @Test
    public void testSearchBackwardsReaderAroundZero() throws IOException {
        createSearchers("xxx", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream("xxx".getBytes()));
        for (Searcher searcher : searchers) {
            List<MatchResult> results = searcher.searchBackwards(data, -1);
            if (!results.isEmpty()) {
                results = searcher.searchBackwards(data, -1);
            }
            assertTrue(searcher.toString(), results.isEmpty());

            results = searcher.searchBackwards(data, 0);
            assertEquals(searcher.toString(),1, results.size());
            assertEquals(searcher.toString(),0, results.get(0).getMatchPosition());
        }
    }

    /**
     * Hard to test prepare commands, as they should have no really visible effect, except to
     * precompute the internal search data.  All we can really do is call it and show that
     * searching still works and no errors are generated.
     */
    @Test
    public void testPrepareBackwards() throws IOException {
        createSearchers("xxxx", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream("xxxxxxx".getBytes()));
        for (Searcher searcher : searchers) {
            searcher.prepareBackwards();

            List<MatchResult> results = searcher.searchBackwards(data, -1);
            assertTrue(searcher.toString(), results.isEmpty());

            results = searcher.searchBackwards(data, 0);
            assertEquals(1, results.size());
            assertEquals(0, results.get(0).getMatchPosition());
        }
    }

    /**
     * Hard to test prepare commands, as they should have no really visible effect, except to
     * precompute the internal search data.  All we can really do is call it and show that
     * searching still works and no errors are generated.
     */
    @Test
    public void testPrepareForwards() {
        createSearchers("xyzz", false);
        byte[] data = "xyzzzzzz".getBytes();
        for (Searcher searcher : searchers) {
            searcher.prepareForwards(); // no error should occur and searching should still work.

            List<MatchResult> results = searcher.searchForwards(data, 1);
            assertTrue(results.isEmpty());

            results = searcher.searchForwards(data, 0);
            assertEquals(1, results.size());
            assertEquals(0, results.get(0).getMatchPosition());
        }
    }

    @Test
    public void testSearchFowardsArrayAroundZero() {
        createSearchers("xyz", false);
        byte[] data = "xyzzzzzz".getBytes();
        for (Searcher searcher : searchers) {
            List<MatchResult> results = searcher.searchForwards(data, 1);
            assertTrue(results.isEmpty());

            results = searcher.searchForwards(data, 0);
            assertEquals(1, results.size());
            assertEquals(0, results.get(0).getMatchPosition());
        }
    }


    @Test
    public void testSearchForwardsReaderAroundZero() throws IOException {
        createSearchers("xxx", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream("xxxyyy".getBytes()));
        for (Searcher searcher : searchers) {
            List<MatchResult> results = searcher.searchForwards(data, 1);
            assertTrue(results.isEmpty());

            results = searcher.searchForwards(data, 0);
            assertEquals(1, results.size());
            assertEquals(0, results.get(0).getMatchPosition());
        }
    }

    // Boundary tests at the end of data.

    @Test
    public void testSearchFowardsArrayBeforeEnd() {
        createSearchers("xyz", false);
        byte[] data = "---xyz".getBytes();
        for (Searcher searcher : searchers) {
            List<MatchResult> results = searcher.searchForwards(data, 0);
            assertEquals(1, results.size());
            assertEquals(3, results.get(0).getMatchPosition());

            results = searcher.searchForwards(data, 0, 2);
            assertTrue(results.isEmpty());

            results = searcher.searchForwards(data, 0, 3);
            assertEquals(1, results.size());
            assertEquals(3, results.get(0).getMatchPosition());
        }
    }

    @Test
    public void testSearchForwardsReaderBeforeEnd() throws IOException {
        createSearchers("xyz", false);
        WindowReader data = new InputStreamReader(new ByteArrayInputStream("---xyz".getBytes()));
        for (Searcher searcher : searchers) {
            List<MatchResult> results = searcher.searchForwards(data, 0);
            assertEquals(1, results.size());
            assertEquals(3, results.get(0).getMatchPosition());

            results = searcher.searchForwards(data, 0, 2);
            assertTrue(results.isEmpty());

            results = searcher.searchForwards(data, 0, 3);
            assertEquals(1, results.size());
            assertEquals(3, results.get(0).getMatchPosition());
        }
    }

    //TODO: tests that searches within particular ranges produce the same results (not entire file...?).

    //TODO: search reader *within* a single array of that window, no crossing...?  flush out array end problems?

    //TODO: need backwards end tests...?


    //TODO: Boundary tests for not matching a pattern whichi is one past or before the search starts/ends (in middle of data).


    //TODO: tests for not matching at position zero when backwards start position is already negative.


    //TODO: fallback searcher tests to demonstrate fallback under wrong length / pathological conditions.

    //TODO: tests for integer overflow - search at ends of very large arrays / Readers (may need test reader for this).

    //TODO: tests for selection of appropriate search length / denial of service defences.

    //TODO: performance tests for MAX_QGRAM limit in Qgram based searchers (currently 4 * TABLE_SIZE).

}
