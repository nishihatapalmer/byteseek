/*
 * Copyright Matt Palmer 2012-17, All rights reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * A static utility class holding useful methods in processing searches and search results.
 * 
 * @author Matt Palmer
 */
public final class SearchUtils {

	/**
	 * The constructor is private to prevent instantiation of a static utility class.
	 */
	private SearchUtils() {
	}

	/**
	 * Searches a byte array forwards for all matches of a {@link Searcher}.
	 *
	 * @param searcher The Searcher to search with.
	 * @param bytes    The byte array to search in.
	 * @return A list of MatchResult objects containing all matches found in the byte array.
	 */
	public static List<MatchResult> searchAllForwards(final Searcher searcher, final byte[] bytes) {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, bytes);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Searches a {@link net.byteseek.io.reader.WindowReader} forwards for all matches of a {@link Searcher}.
	 *
	 * @param searcher The Searcher to search with.
	 * @param reader   The WindowReader to search in.
	 * @return A list of MatchResult objects containing all matches found in the WindowReader.
	 */
	public static List<MatchResult> searchAllForwards(final Searcher searcher, final WindowReader reader) {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final ForwardSearchIterator iterator = new ForwardSearchIterator(searcher, reader);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Searches a byte array backwards for all matches of a {@link Searcher}.
	 *
	 * @param searcher The Searcher to search with.
	 * @param bytes    The byte array to search in.
	 * @return A list of MatchResult objects containing all matches found in the byte array.
	 */
	public static  List<MatchResult> searchAllBackwards(final Searcher searcher, final byte[] bytes) {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, bytes);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

	/**
	 * Searches a {@link net.byteseek.io.reader.WindowReader} forwards for all
	 * matches of a {@link Searcher}.
	 *
	 * @param searcher The Searcher to search with.
	 * @param reader   The WindowReader to search in.
	 * @return A list of MatchResult objects containing all matches found in the WindowReader.
	 * @throws IOException if a problem occurred reading in the WindowReader.
	 */
	public static  List<MatchResult> searchAllBackwards(final Searcher searcher, final WindowReader reader) throws IOException {
		final List<MatchResult> results = new ArrayList<MatchResult>();
		final BackwardSearchIterator iterator = new BackwardSearchIterator(searcher, reader);
		while (iterator.hasNext()) {
			results.addAll(iterator.next());
		}
		return results;
	}

}
