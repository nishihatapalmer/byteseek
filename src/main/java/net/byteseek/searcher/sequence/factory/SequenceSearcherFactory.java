/*
 * Copyright Matt Palmer 2017-20, All rights reserved.
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
 */
package net.byteseek.searcher.sequence.factory;

import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.searcher.sequence.SequenceSearcher;

/**
 * An interface for factories which create an appropriate SequenceSearcher given a byte, a ByteMatcher,
 * a regular expression, or a SequenceMatcher.
 * <p>
 * There is no single best search algorithm.  It varies depending on what is being searched for, how long that is,
 * how long the thing you are looking in is, how big the alphabet is, can you match more than one thing (e.g. case
 * insensitive matching), and quite a few more things too.
 * <p>
 * Some common factory strategies are provided as public static members.  These include:
 *  SELECT_BY_LENGTH - choose the searcher depending on how long the pattern is.
 **/
public interface SequenceSearcherFactory {

   /**
    * Creates a SequenceSearcher for a single byte value.
    *
    * @param theByte The byte to search for.
    * @return A SequenceSearcher for the byte.
    */
   SequenceSearcher create(byte theByte);

   /**
    * Creates a SequenceSearcher given a ByteMatcher.
    *
    * @param theMatcher The ByteMatcher to search for.
    * @return A SequenceSearcher for the ByteMatcher.
    * @throws IllegalArgumentException if the ByteMatcher is null.
    */
   SequenceSearcher create(ByteMatcher theMatcher);

   /**
    * Creates a SequenceSearcher for an array of bytes.
    *
    * @param theBytes the array of bytes to search for.
    * @return A SequenceSearcher for the byte array.
    * @throws IllegalArgumentException if the byte array is null or empty.
    */
   SequenceSearcher create(byte[] theBytes);

   /**
    * Creates a SequenceSearcher to search forwards given a SequenceMatcher.
    * This can be different from the backwards searcher, since search algorithms are
    * often sensitive to where wildcards or large sets of bytes are placed in a pattern.
    *
    * @param theSequence The SequenceMatcher to search for.
    * @return A SequenceSearcher giving good performance for the SequenceMatcher.
    * @throws IllegalArgumentException if the SequenceMatcher is null.
    */
   SequenceSearcher createForwards(SequenceMatcher theSequence);

   /**
    * Creates a SequenceSearcher to search backwards given a SequenceMatcher.
    * This can be different from the forwards searcher, since search algorithms are
    * often sensitive to where wildcards or large sets of bytes are placed in a pattern.
    *
    * @param theSequence The SequenceMatcher to search for.
    * @return A SequenceSearcher giving good performance for the SequenceMatcher.
    * @throws IllegalArgumentException if the SequenceMatcher is null.
    */
   SequenceSearcher createBackwards(SequenceMatcher theSequence);

}
