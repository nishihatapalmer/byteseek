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
import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by matt on 05/05/17.
 */
public class GeneralSearcherTests extends SearchersToTest {

    @Test
    public void testSearchForwardsEmptyByteArray() {
        createSearchers("x");
        byte[] data = new byte[0];
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            try {
                searcher.searchSequenceForwards(data);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsEmptyByteArray() {
        createSearchers("x");
        byte[] data = new byte[0];
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            try {
                searcher.searchSequenceBackwards(data);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }

    @Test
    public void testSearchForwardsEmptyReader() {
        createSearchers("x");
        WindowReader data = new ByteArrayReader(new byte[0]);
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            try {
                searcher.searchSequenceForwards(data);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex.getCause());
            }
        }
    }


    @Test
    public void testSearchBackwardsEmptyReader() {
        createSearchers("x");
        WindowReader data = new ByteArrayReader(new byte[0]);
        for (SequenceSearcher<SequenceMatcher> searcher : searchers) {
            try {
                searcher.searchSequenceBackwards(data);
            } catch (Exception ex) {
                fail("Searcher " + searcher + " had exception " + ex);
            }
        }
    }

}
