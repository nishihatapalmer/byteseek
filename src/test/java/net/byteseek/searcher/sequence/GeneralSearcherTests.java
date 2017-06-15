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
package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.ByteArrayReader;
import net.byteseek.io.reader.WindowReader;
import org.junit.Test;

import java.io.IOException;

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
        createSearchers("x");
        byte[] data = new byte[0];
        for (SequenceSearcher searcher : searchers) {
            try {
                int result = searcher.searchSequenceForwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsEmptyByteArray() {
        createSearchers("x");
        byte[] data = new byte[0];
        for (SequenceSearcher searcher : searchers) {
            try {
                int result = searcher.searchSequenceBackwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }

    @Test
    public void testSearchForwardsEmptyReader() {
        createSearchers("x");
        WindowReader data = new ByteArrayReader(new byte[0]);
        for (SequenceSearcher searcher : searchers) {
            try {
                long result = searcher.searchSequenceForwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsEmptyReader() {
        createSearchers("x");
        WindowReader data = new ByteArrayReader(new byte[0]);
        for (SequenceSearcher searcher : searchers) {
            try {
                long result = searcher.searchSequenceBackwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex);
            }
        }
    }

    // Tests where pattern is longer than the data.

    @Test
    public void testSearchForwardsShortByteArray() {
        createSearchers("xyz123abc");
        byte[] data = new byte[2];
        for (SequenceSearcher searcher : searchers) {
            try {
                int result = searcher.searchSequenceForwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsShortByteArray() {
        createSearchers("xyz123abc");
        byte[] data = new byte[2];
        for (SequenceSearcher searcher : searchers) {
            try {
                int result = searcher.searchSequenceBackwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }

    @Test
    public void testSearchForwardsShortReader() {
        createSearchers("xyz123abc");
        WindowReader data = new ByteArrayReader(new byte[2]);
        for (SequenceSearcher searcher : searchers) {
            try {
                long result = searcher.searchSequenceForwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsShortReader() {
        createSearchers("xyz123abc");
        WindowReader data = new ByteArrayReader(new byte[2]);
        for (SequenceSearcher searcher : searchers) {
            try {
                long result = searcher.searchSequenceBackwards(data);
                assertTrue("searcher " + searcher, result < 0);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex);
            }
        }
    }

    // Boundary tests around the start of data.

    @Test
    public void testSearchBackwardsArrayAroundZero() {
        createSearchers("xyz");
        byte[] data = "xyz".getBytes();
        for (SequenceSearcher searcher : searchers) {
            int result = searcher.searchSequenceBackwards(data, -1);
            assertTrue("searcher " + searcher, result < 0);
            result = searcher.searchSequenceBackwards(data, 0);
            assertEquals("searcher " + searcher, result, 0);
        }
    }

    @Test
    public void testSearchBackwardsReaderAroundZero() throws IOException {
        createSearchers("xxx");
        WindowReader data = new ByteArrayReader("xxx");
        for (SequenceSearcher searcher : searchers) {
            long result = searcher.searchSequenceBackwards(data, -1);
            assertTrue("searcher " + searcher, result < 0);
            result = searcher.searchSequenceBackwards(data, 0);
            assertEquals("searcher " + searcher, result, 0);
        }
    }

    @Test
    public void testSearchFowardsArrayAroundZero() {
        createSearchers("xyz");
        byte[] data = "xyzzzzzz".getBytes();
        for (SequenceSearcher searcher : searchers) {
            int result = searcher.searchSequenceForwards(data, 1);
            assertTrue("searcher " + searcher, result < 0);
            result = searcher.searchSequenceForwards(data, 0);
            assertEquals("searcher " + searcher, 0, result);
        }
    }

    @Test
    public void testSearchForwardsReaderAroundZero() throws IOException {
        createSearchers("xxx");
        WindowReader data = new ByteArrayReader("xxxyyy");
        for (SequenceSearcher searcher : searchers) {
            long result = searcher.searchSequenceForwards(data, 1);
            assertTrue("searcher " + searcher, result < 0);
            result = searcher.searchSequenceForwards(data, 0);
            assertEquals("searcher " + searcher, 0, result);
        }
    }

    // Boundary tests at the end of data.

    @Test
    public void testSearchFowardsArrayBeforeEnd() {
        createSearchers("xyz");
        byte[] data = "---xyz".getBytes();
        for (SequenceSearcher searcher : searchers) {
            int result = searcher.searchSequenceForwards(data, 0);
            assertEquals("searcher " + searcher, 3, result);
            result = searcher.searchSequenceForwards(data, 0, 2);
            assertTrue("searcher " + searcher, result < 0);
            result = searcher.searchSequenceForwards(data, 0, 3);
            assertEquals("searcher " + searcher, 3, result);
        }
    }

    @Test
    public void testSearchForwardsReaderBeforeEnd() throws IOException {
        createSearchers("xyz");
        WindowReader data = new ByteArrayReader("---xyz");
        for (SequenceSearcher searcher : searchers) {
            long result = searcher.searchSequenceForwards(data, 0);
            assertEquals("searcher " + searcher, 3, result);
            result = searcher.searchSequenceForwards(data, 0, 2);
            assertTrue("searcher " + searcher, result < 0);
            result = searcher.searchSequenceForwards(data, 0, 3);
            assertEquals("searcher " + searcher, 3, result);
        }
    }

    //TODO: need backwards end tests...?


    //TODO: Boundary tests for not matching a pattern whichi is one past or before the search starts/ends (in middle of data).



}
