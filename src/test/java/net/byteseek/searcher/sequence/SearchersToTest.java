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

import net.byteseek.compiler.CompileException;
import net.byteseek.compiler.matcher.SequenceMatcherCompiler;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.ByteSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.parser.regex.RegexParser;
import net.byteseek.searcher.bytes.ByteMatcherSearcher;
import net.byteseek.searcher.bytes.ByteSearcher;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * A  base class for multi-searcher tests which sets up all the search classes for test.
 *
 * Created by matt on 06/05/17.
 */
public class SearchersToTest {

    private final static SequenceMatcherCompiler compiler = new SequenceMatcherCompiler();

    public List<SequenceSearcher> searchers;

    /**
     * Instantiate the searchers we want to test with the sequence matcher passed in.
     *
     * @param sequence The sequence matcher to search for.
     */
    public void createSearchers(SequenceMatcher sequence, boolean lowAlphabet) {
        searchers = new ArrayList<SequenceSearcher>();
        searchers.add(new SequenceMatcherSearcher(sequence));
        searchers.add(new SundayQuickSearcher(sequence));
        searchers.add(new HorspoolSearcher(sequence));
        searchers.add(new HorspoolUnrolledSearcher(sequence));
        searchers.add(new SignedHorspoolSearcher(sequence));
        searchers.add(new SignedHash2Searcher(sequence));
        searchers.add(new SignedHash3Searcher(sequence));
        searchers.add(new SignedHash4Searcher(sequence));
        searchers.add(new ShiftOrSearcher(sequence));
        searchers.add(new ShiftOrUnrolledSearcher(sequence));

        //TODO: on low alphabets and long patterns (e.g. human dna) these searchers perform *incredibly* poorly.
        // I disable them from full testing when the sequence length gets too long, otherwise running a lot of tests takes hours
        // to complete.  Should run more tests of these algorithms on shorter patterns to achieve the same test coverage,
        // and occasionally run longer pattern tests on them to ensure longer patterns still work for them.
        if (!lowAlphabet || sequence.length() < 200) {
            searchers.add(new QgramFilter2Searcher(sequence));
        }
        if (!lowAlphabet || sequence.length() < 800) {
            searchers.add(new QgramFilter3Searcher(sequence));
        }
        if (!lowAlphabet || sequence.length() < 4000) {
            searchers.add(new QgramFilter4Searcher(sequence));
        }

        // Include byte searchers for low length searches.
        if (sequence.length() == 1) {
            ByteMatcher matcher = sequence.getMatcherForPosition(0);
            searchers.add(new ByteMatcherSearcher(matcher));
            if (matcher.getNumberOfMatchingBytes() == 1) {
                searchers.add(new ByteSearcher(matcher.getMatchingBytes()[0]));
            }
        }
    }


    /**
     * Creates searchers for case insensitive versions of a string.
     *
     * Any backticks within the string itself (which are the open/close quote characters of
     * a case insensitive string in byteseek regular expressions), are replaced by the hex
     * value of the backtick, with the rest of the string as case insensitive.  This
     * ensures that all strings passed in can be recognised, even if they contain the case
     * insensitive quote char (backtick).
     *
     * @param sequence The string to search for case insensitively.
     * @throws CompileException if a problem occurs compiling the sequence.
     */
    public void createCaseInsensitiveSearchers(String sequence, boolean lowAlphabet) throws CompileException {
        final String encodedString = RegexParser.encodeCaseInsensitiveString(sequence);
        createSearchers(compiler.compile(encodedString), lowAlphabet);
    }


    public void createCaseInsensitiveSearchers(byte[] sequence, boolean lowAlphabet) throws CompileException {
        final String newString = new String(sequence, Charset.forName("ISO-8859-1"));
        createCaseInsensitiveSearchers(newString, lowAlphabet);
    }

    /**
     * Create searchers for the ASCII byte values of the string passed in.
     *
     * @param sequence the ASCII string to search for.
     */
    public void createSearchers(String sequence, boolean lowAlphabet) {
        createSearchers(new ByteSequenceMatcher(sequence.getBytes()), lowAlphabet);
    }


    /**
     * Create searchers for the byte sequence passed in.
     *
     * @param sequence The byte sequence to search for.
     */
    public void createSearchers(byte[] sequence, boolean lowAlphabet) {
        createSearchers(new ByteSequenceMatcher(sequence), lowAlphabet);
    }


}
