/*
 * Copyright Matt Palmer 2009-2013, All rights reserved.
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

package net.byteseek.compiler.matcher;

import java.util.Collection;

import net.byteseek.matcher.bytes.ByteMatcher;

/**
 * An interface for objects which implement a factory for {@link ByteMatcher}s.
 *
 * @author Matt Palmer
 */
public interface ByteMatcherFactory {

    /**
     * A constant to say that a set of bytes passed in to the factory should be inverted.
     */
    public final static boolean INVERTED = true;
    
    
    /**
     * A constant to say that a set of bytes passed in to the factory should not be inverted.
     */
    public final static boolean NOT_INVERTED = false;
    
    
    /**
     * Creates a {@link  ByteMatcher} from the collection of bytes passed in.
     * There may be duplicate values in the collection.
     * 
     * @param bytes A collection of bytes to match.
     * @return A ByteMatcher which matches that set of bytes.
     */
    ByteMatcher create(Collection<Byte> bytes);
    
    
    /**
     * Creates a {@link  ByteMatcher} from the collection of bytes passed in.
     * There may be duplicate values in the collection.
     * 
     * @param byteSet A collection of bytes to match
     * @param inverted Whether to invert the set of bytes to match.
     * @return A ByteMatcher which matches the set of bytes (or their inverse, if
     *         specified in the inverted parameter).
     */
    ByteMatcher create(Collection<Byte> byteSet, boolean inverted);

}
