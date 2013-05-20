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
package net.byteseek.searcher;

/**
 * A simple immutable class holding a single result of a search.
 * <p>
 * It contains the position that a match was found at, and the matching object
 * which is associated with that position.
 * 
 * @param <T>
 *            The type of object to return for a match.
 * 
 * @author Matt Palmer
 */
public final class SearchResult<T> {

	private final long matchPosition;
	private final T matchingObject;

	/**
	 * Constructs a SearchResult from a position and the object which matched.
	 * 
	 * @param matchPosition
	 *            The position a match was found at.
	 * @param matchingObject
	 *            The object associated with the match.
	 */
	public SearchResult(final long matchPosition, final T matchingObject) {
		this.matchPosition = matchPosition;
		this.matchingObject = matchingObject;
	}

	/**
	 * Returns the position a match was found at.
	 * 
	 * @return long The position a match was found at.
	 */
	public long getMatchPosition() {
		return matchPosition;
	}

	/**
	 * Returns the object associated with the match.
	 * 
	 * @return T The object associated with the match.
	 */
	public T getMatchingObject() {
		return matchingObject;
	}

}
