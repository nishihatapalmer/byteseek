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
package net.byteseek.searcher.sequence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import net.byteseek.io.FileReader;
import net.byteseek.io.WindowReader;
import net.byteseek.matcher.bytes.AnyByteMatcher;
import net.byteseek.matcher.sequence.ByteArrayMatcher;
import net.byteseek.matcher.sequence.CaseInsensitiveSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.ForwardSearchIterator;
import net.byteseek.searcher.SearchResult;
import net.byteseek.searcher.Searcher;
import net.byteseek.searcher.sequence.BoyerMooreHorspoolSearcher;

/**
 *
 * @author Matt Palmer
 */
public class SequenceSearcherTest {

	@Test
	public void scratchTesting() throws FileNotFoundException, IOException {

		SequenceMatcher matcher = new ByteArrayMatcher("A Midsommer Nights Dreame");
		Searcher<SequenceMatcher> searcher = new BoyerMooreHorspoolSearcher(matcher);
		findMatches(searcher, 0);

		SequenceMatcher caseMatcher = new CaseInsensitiveSequenceMatcher(
				"A Midsommer Nights Dreame");
		Searcher<SequenceMatcher> caseSearcher = new BoyerMooreHorspoolSearcher(caseMatcher);
		findMatches(caseSearcher, 0);

		caseMatcher = new CaseInsensitiveSequenceMatcher("MIDSOMMER NIGHT"); // fails its own length from the end if you add the H.
		caseSearcher = new BoyerMooreHorspoolSearcher(caseMatcher);
		findMatches(caseSearcher, 112236);

		caseSearcher = new BoyerMooreHorspoolSearcher(AnyByteMatcher.ANY_BYTE_MATCHER);
		findMatches(caseSearcher, 112236);
	}

	private void findMatches(Searcher<SequenceMatcher> searcher, final long searchPosition)
			throws FileNotFoundException, IOException {
		final WindowReader reader = new FileReader(getFile("/TestASCII.txt"));
		final ForwardSearchIterator<SequenceMatcher> searchIterator = new ForwardSearchIterator<SequenceMatcher>(
				searcher, reader, searchPosition);
		while (searchIterator.hasNext()) {
			final List<SearchResult<SequenceMatcher>> results = searchIterator.next();
			for (final SearchResult<SequenceMatcher> result : results) {
				String message = String.format("Match found at:%d", result.getMatchPosition());
				System.out.println(message);
			}
		}
		System.out.println();
	}

	private File getFile(final String resourceName) {
		URL url = this.getClass().getResource(resourceName);
		return new File(url.getPath());
	}

}
