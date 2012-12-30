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
package net.domesdaybook.matcher.automata;

import java.io.IOException;
import java.util.Collection;

import net.domesdaybook.io.WindowReader;
import net.domesdaybook.matcher.MatchResult;
import net.domesdaybook.matcher.Matcher;

/**
 * An interface for matchers which match Automata, extending the Matcher
 * interface with methods that return a collection of {@link MatchResult}s (or
 * an empty collection if no objects matched at that position). It also
 * implements Iterable<MatchResult<T>> to provide a way to successively return
 * matches for a position.
 * 
 * @param T
 *              The type of object associated with matching states in the Automata.
 * @author Matt palmer
 */
public interface AutomataMatcher<T> extends Matcher {

	/**
	 * Returns the first {@link MatchResult} for the position given, or null if no objects matched. 
	 * If the position is out of bounds for the WindowReader (before the start or after the end), 
	 * then no match will be found, and no exception will be thrown.
	 * <p>
	 * This method will only return the first match it finds. An automata can
	 * match more than one set of objects, which may exist in later States of the 
	 * Automata (but at the same fundamental position in the WindowReader). To match the next
	 * match in the Automata, call {@link #nextMatch(WindowReader, MatchResult).
	 * To match all objects the automata can match at the given position, use the method
	 * {@link #allMatches(WindowReader, long).
	 * 
	 * @param reader
	 *            The {@link WindowReader} to match in.
	 * @param matchPosition
	 *            The position to attempt a match at.
	 * @return A MatchResult, or null if no match occurred.
	 * 
	 * @throws IOException
	 *             If there was a problem reading bytes in the WindowReader.
	 */
	public MatchResult<T> firstMatch(WindowReader reader, long matchPosition) throws IOException;

	/**
	 * Returns the next {@link MatchResult} for the last MatchResult given, or null if no objects matched. 
	 * <p>
	 * This method will only return the next match it finds. An automata can
	 * match more than one set of objects, which may exist in later States of the 
	 * Automata (but at the same fundamental position in the WindowReader). 
	 * To match all objects the automata can match at the given position, use the method
	 * {@link #allMatches(WindowReader, long)
	 *
	 * @param reader
	 *            The {@link WindowReader} to match in.
	 * @param lastMatch
	 *            The last MatchResult you have.
	 * @return A MatchResult, or null if no match occurred.
	 * 
	 * @throws IOException
	 *             If there was a problem reading bytes in the WindowReader.
	 */
	public MatchResult<T> nextMatch(WindowReader reader, MatchResult<T> lastMatch) throws IOException;

	/**
	 * Returns a collection of {@link MatchResult}s at the position given, or an
	 * empty collection if no objects matched. If the position is out of bounds
	 * for the WindowReader (before the start or after the end), then no match will be
	 * found, and no exception will be thrown.
	 * <p>
	 * This method returns all the objects that the automata can match at the
	 * given position in the WindowReader.
	 * 
	 * @param reader
	 * 				The {@link WindowReader} to match in.
	 * @param matchPosition
	 * 				The position to attempt a match at.
	 * @return A Collection of MatchResults, or an empty collection if no
	 *         objects matched.
	 * 
	 * @throws IOException
	 * 				If there was a problem reading bytes in the WindowReader.
	 */
	public Collection<MatchResult<T>> allMatches(WindowReader reader, long matchPosition)
			throws IOException;

	/**
	 * Returns a collection of {@link MatchResult}s at the position given, or an
	 * empty collection if no objects matched. If the position is out of bounds
	 * for the byte array (before the start or after the end), then no match
	 * will be found, and no exception will be thrown.
	 * <p>
	 * This method will only return the first matches it finds. An automata can
	 * match more than one set of objects, which may exist in later States of the 
	 * Automata (but at the same fundamental position in the WindowReader). 
	 * To match all objects the automata can match at the given position, use the method
	 * {@link #allMatches(byte[], int)
	 * 
	 * @param bytes
	 *            The byte array to match in.
	 * @param matchPosition
	 *            The position to attempt a match at.
	 * @return A MatchResult, or null if no match was found.
	 */
	public MatchResult<T> firstMatch(byte[] bytes, int matchPosition);

	/**
	 * Returns the next {@link MatchResult} for the last MatchResult given, or null if no objects matched. 
	 * <p>
	 * This method will only return the next match it finds. An automata can
	 * match more than one set of objects, which may exist in later States of the 
	 * Automata (but at the same fundamental position in the WindowReader). 
	 * To match all objects the automata can match at the given position, use the method
	 * {@link #allMatches(byte[], int)
	 
	 * @param bytes
	 *            The byte array to match in.
	 * @param lastMatch
	 *            The last MatchResult you have.
	 * @return A MatchResult, or null if no match occurred.
	 */
	public MatchResult<T> nextMatch(byte[] bytes, MatchResult<T> lastMatch);

	/**
	 * Returns a collection of {@link MatchResult}s at the position given, or an
	 * empty collection if no objects matched. If the position is out of bounds
	 * for the byte array (before the start or after the end), then no match
	 * will be found, and no exception will be thrown.
	 * <p>
	 * This method returns all the objects that the automata can match at the
	 * given position in the byte array.
	 * 
	 * @param bytes
	 *            The byte array to match in.
	 * @param matchPosition
	 *            The position to attempt a match at.
	 * @return A Collection of MatchResults, or an empty collection if no
	 *         objects matched.
	 */
	public Collection<MatchResult<T>> allMatches(byte[] bytes, int matchPosition);

}
