/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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

import net.byteseek.io.WindowReader;

/**
 * An interface for classes that can match bytes from a given position, either
 * directly within a byte array, or through a WindowReader object.
 * 
 * @author Matt Palmer
 */
public interface Matcher {

	/**
	 * Returns whether there is a match or not at the given position in a
	 * WindowReader. If the position to match at does not exist in the WindowReader, then no
	 * exception is thrown - there will simply be no match.
	 * 
	 * @param reader
	 *            The {@link WindowReader} to read from.
	 * @param matchPosition
	 *            The position to try to match at.
	 * @return Whether there is a match at the given position.
	 * @throws IOException
	 *             if the WindowReader cannot read (but not for reads past the end of
	 *             the WindowReader).
	 */
	public boolean matches(WindowReader reader, long matchPosition)
			throws IOException;

	/**
	 * Returns whether there is a match or not at the given position in a byte
	 * array. If the position to match at does not exist in the byte array, then
	 * no exception is thrown - there will simply be no match.
	 * 
	 * @param bytes
	 *            An array of bytes to read from.
	 * @param matchPosition
	 *            The position to try to match at.
	 * @return Whether there is a match at the given position.
	 */
	public boolean matches(byte[] bytes, int matchPosition);
}
