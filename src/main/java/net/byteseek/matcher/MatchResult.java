/*
 * Copyright Matt Palmer 2013, All rights reserved.
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

import java.util.Collection;

/**
 * An interface for a simple data carrying class to hold the results of matching something at a
 * given position, with a length and a collection of associated matching objects.
 * <p>
 * Many matchers will not use the MatchResult interface, as what matched and how
 * long the match is implicit from the type of matching being done. For
 * example, when matching sequences, it is obvious what matched and how long the
 * match was, so there is no need to introduce an intermediate result object.
 * However, some matchers can match more than once, and may be associated with
 * different objects for different stages of matching. These types of matchers
 * will typically return a MatchResult or a collection of them.
 * 
 * @param <T> The type of object associated with the match.
 * 
 * @author Matt Palmer
 */
public interface MatchResult<T> {

	/**
	 * Returns the position a match was found at.
	 * 
	 * @return The position a match was found at.
	 */
	public long getMatchPosition();

	
	/**
	 * Returns the length of the match.
	 * 
	 * @return The length of the match.
	 */
	public long getMatchLength();

	
	/**
	 * Returns the objects associated with the match.
	 * 
	 * @return The objects associated with the match.
	 */
	public Collection<T> getMatchingObjects();
	
}
