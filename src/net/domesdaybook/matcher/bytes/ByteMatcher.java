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

package net.domesdaybook.matcher.bytes;

import net.domesdaybook.matcher.sequence.SequenceMatcher;

/**
 * An interface for classes which attempt to match a single byte in some manner.
 * <p>
 * It extends {@link SequenceMatcher} as the special case of a sequence with
 * a fixed length of one.
 * <p>
 * All implementations of this interface should be immutable.
 * This allows them to be safely shared amongst other classes and threads.
 * 
 * @author Matt Palmer
 */
public interface ByteMatcher extends SequenceMatcher {

    /**
     * Implementations of this method should strive to be as efficient as possible.
     *
     * @param theByte The byte to match.
     * @return boolean Whether the byte matches the byte matcher.
     */
    boolean matches(final byte theByte);

    
    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return byte[] An array of all the bytes that this byte matcher could match.
     */
    byte[] getMatchingBytes();


    /**
     * Implementations of this method can be calculated dynamically,
     * and may not be efficient if called repeatedly.
     *
     * @return int The number of bytes this byte matcher will match.
     */
    int getNumberOfMatchingBytes();
    

}
