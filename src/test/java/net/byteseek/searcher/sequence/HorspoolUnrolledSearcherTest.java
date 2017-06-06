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

import net.byteseek.matcher.sequence.SequenceMatcher;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * A test of non-search methods in the HorspoolUnrolledSearcher.
 *
 * Search methods are comprehensively tested (and cross validated) across all the searchers in other test classes.
 */
public class HorspoolUnrolledSearcherTest {

    // Test constructors

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullSequence() {
        new HorspoolUnrolledSearcher((SequenceMatcher) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullString() {
        new HorspoolUnrolledSearcher((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyString() {
        new HorspoolUnrolledSearcher("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullCharset() {
        new HorspoolUnrolledSearcher("ABCDEFG", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructNullByteArray() {
        new HorspoolUnrolledSearcher((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructEmptyByteArray() {
        new HorspoolUnrolledSearcher(new byte[0]);
    }

    // Test length.

    @Test
    public void testGetSequenceLength() throws Exception {
        AbstractSequenceSearcher s = new HorspoolUnrolledSearcher("A");
        assertEquals("Length correct", 1, s.getSequenceLength() );

        s = new HorspoolUnrolledSearcher("AA");
        assertEquals("Length correct", 2, s.getSequenceLength() );

        s = new HorspoolUnrolledSearcher("1234567890");
        assertEquals("Length correct", 10, s.getSequenceLength() );
    }
}