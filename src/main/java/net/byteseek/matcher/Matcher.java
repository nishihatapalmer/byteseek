/*
 * Copyright Matt Palmer 2009-2017, All rights reserved.
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

import java.io.IOException;
import java.util.List;

import net.byteseek.io.reader.WindowReader;

/**
 * An interface for classes that can match bytes from a given position, either
 * directly within a byte array, or through a WindowReader object.
 * 
 * @author Matt Palmer
 */
public interface Matcher {

	/**
	 * Returns the number of matches at the given position in a WindowReader.
	 * The actual match results are added to a list passed in to the method.
	 * If the position to match at does not exist in the WindowReader, then no
	 * exception is thrown - there will simply be no matches.
	 *
	 * @param reader        The {@link WindowReader} to read from.
	 * @param matchPosition The position to try to match at.
	 * @param results       The list of match results to append new matches to.
	 * @return              The number of match results for the position.
	 * @throws IOException  if the WindowReader cannot read (but not for reads past the end of the WindowReader).
	 * @throws NullPointerException if the WindowReader is null.
	 */
	int matches(WindowReader reader, long matchPosition, List<MatchResult> results) throws IOException;

	/**
	 * Returns whether there is a match or not at the given position in a
	 * WindowReader. If the position to match at does not exist in the WindowReader, then no
	 * exception is thrown - there will simply be no match.
	 * 
	 * @param reader        The {@link WindowReader} to read from.
	 * @param matchPosition The position to try to match at.
	 * @return Whether there is a match at the given position.
	 * @throws IOException  if the WindowReader cannot read (but not for reads past the end of the WindowReader).
	 * @throws NullPointerException if the WindowReader is null.
	 */
	boolean matches(WindowReader reader, long matchPosition) throws IOException;

	/**
	 * Returns whether the number of matches at the given position in a byte
	 * array. If the position to match at does not exist in the byte array, then
	 * no exception is thrown - there will simply be no match.
	 * The actual matches are added to a list of match results passed in to the mathod.
	 *
	 * @param bytes         An array of bytes to read from.
	 * @param matchPosition The position to try to match at.
	 * @param results       A list of MatchResults to append any matches to.
	 * @return              The number of matches at that position.
	 * @throws NullPointerException if the byte array passed in is null.
	 */
	int matches(byte[] bytes, int matchPosition, List<MatchResult> results);

	/**
	 * Returns whether there is a match or not at the given position in a byte
	 * array. If the position to match at does not exist in the byte array, then
	 * no exception is thrown - there will simply be no match.
	 * 
	 * @param bytes         An array of bytes to read from.
	 * @param matchPosition The position to try to match at.
	 * @return Whether there is a match at the given position.
	 * @throws NullPointerException if the byte array passed in is null.
	 */
	boolean matches(byte[] bytes, int matchPosition);
}
