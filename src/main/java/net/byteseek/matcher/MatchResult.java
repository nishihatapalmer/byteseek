/*
 * Copyright Matt Palmer 2013-21, All rights reserved.
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
package net.byteseek.matcher;

import net.byteseek.io.IOIterator;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.utils.ArgUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * A simple data carrying class to hold the results of matching something at a
 * given position, with a match length.  It has some useful methods to obtain the data it matches,
 * given the byte array or WindowReader that was used to match on.
 *
 * <p>
 * This class is suitable for extension by subclassing.
 * It is immutable - subclasses can only add data and behaviour, not change existing data or behaviour.
 *
 * @author Matt Palmer
 */
public class MatchResult {

	private final long matchPosition;
	private final long matchLength;

	/**
	 * Constructs a MatchResult given a match position and a match length.
	 *
	 * @param matchPosition The position a match was found at.
	 * @param matchLength   The length of the match.
	 */
	public MatchResult(final long matchPosition, final long matchLength) {
		this.matchPosition = matchPosition;
		this.matchLength   = matchLength;
	}

	/**
	 * Returns the position a match was found at.
	 * 
	 * @return The position a match was found at.
	 */
	public final long getMatchPosition() {
		return matchPosition;
	}

	/**
	 * Returns the length of the match.
	 * 
	 * @return The length of the match.
	 */
	public final long getMatchLength() {
		return matchLength;
	}

	/**
	 * Returns a copy of the data matched by the match result, given the byte array it was matched in.
	 *
	 * @param source The byte array that the MatchResult was matched in.
	 * @return A byte array containing the data from the byte array for this MatchResult.
	 * @throws IllegalArgumentException if the source is null.
	 * @throws IndexOutOfBoundsException if the match position and length do not fit into the source byte array.
	 */
	public final byte[] bytes(final byte[] source) {
		ArgUtils.checkNullObject(source, "source");
		ArgUtils.checkIndexOutOfBounds(source.length, matchPosition, matchPosition + matchLength);
		return Arrays.copyOfRange(source, (int) matchPosition, (int) (matchPosition + matchLength));
	}

	/**
	 * Returns a copy of the data matched by the MatchResult, given the WindowReader it was matched in,
	 * as an iterator over byte arrays (since it is possible to match more data than a single byte array
	 * can handle, and in practice, they will be cached in blocks (e.g. 4kb), and returned as such.
	 *
	 * @param source The WindowReader that the MatchResult was matched in.
	 * @return An iterator over byte arrays containing the data for this MatchResult.
	 * @throws IOException if the bytes cannot be read or the reader is closed.
	 * @throws IllegalArgumentException if the source is null.
	 */
	public final IOIterator<byte[]> bytes(final WindowReader source) throws IOException {
		return source.bytes(matchPosition, matchPosition + matchLength - 1);
	}

	/**
	 * Returns a copy of the data matched by the MatchResult, given the WindowReader it was matched in,
	 * as a single byte array.  Note that if the amount of data matched by this MatchResult exceeds
	 * Integer.MAX_VALUE, then an IllegalArgumentException will be thrown, as this cannot fit into a single byte array.
	 *
	 * @param source The WindowReader that the MatchResult was matched in.
	 * @return A byte array containing the MatchResult data.
	 * @throws IOException if the bytes cannot be read or the reader is closed.
	 * @throws IllegalArgumentException if the source is null, or the amount of data in the match exceeds Integer.MAX_VALUE.
	 */
	public final byte[] allBytes(final WindowReader source) throws IOException {
		return source.allBytes(matchPosition, matchPosition + matchLength - 1);
	}

	@Override
 	public int hashCode() {
		return (int) (matchPosition * matchLength);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof MatchResult)) {
			return false;
		}

		final MatchResult other = (MatchResult) obj;
		return matchPosition == other.matchPosition &&
			   matchLength   == other.matchLength;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(position:" + matchPosition + " length:" + matchLength + ')';
	}
	
}
