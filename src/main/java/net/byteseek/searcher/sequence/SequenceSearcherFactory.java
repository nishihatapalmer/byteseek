/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.searcher.sequence;

import net.byteseek.compiler.CompileException;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;

/**
 * An interface for factories which create an appropriate SequenceSearcher given a byte, a ByteMatcher,
 * a regular expression, or a SequenceMatcher.
 */
public interface SequenceSearcherFactory {

   /**
    * The default SequenceSearcherFactory, which should give reasonable performance in most cases.
    */
   public final static SequenceSearcherFactory DEFAULT_FACTORY = new SequenceSearcherSimpleFactory();

   /**
    * Creates a SequenceSearcher for a single byte value.
    *
    * @param theByte The byte to search for.
    * @return A SequenceSearcher for the byte.
    */
   SequenceSearcher create(byte theByte);

   /**
    * Creates a SequenceSearcher for an array of bytes.
    *
    * @param theBytes the array of bytes to search for.
    * @return A SequenceSearcher for the byte array.
    * @throws IllegalArgumentException if the byte array is null or empty.
    */
   SequenceSearcher create(byte[] theBytes);

   /**
    * Creates a SequenceSearcher given a regular expression using byteseek regex format.
    * This may simply be a string of hex bytes (not case sensitive, no 0x prefix), or it
    * may include more advanced syntax such as sets, ranges or other fixed-length constructs.
    * It cannot include any regular expression syntax which leads to variable length matching
    * or which has alternative sequences in it.
    *
    * @param regex The regular expression to search for.  The regex
    * @return A SequenceSearcher for the regular expression.
    * @throws CompileException if the regular expression could not be parsed or compiled into a fixed length sequence.
    * @throws IllegalArgumentException if the regex is null or empty.
    */
   SequenceSearcher create(String regex) throws CompileException;

   /**
    * Creates a SequenceSearcher given a ByteMatcher.
    *
    * @param theMatcher The ByteMatcher to search for.
    * @return A SequenceSearcher for the ByteMatcher.
    * @throws IllegalArgumentException if the ByteMatcher is null.
    */
   SequenceSearcher create(ByteMatcher theMatcher);

   /**
    * Creates a SequenceSearcher given a SequenceMatcher.  It will select from different searchers depending on
    * the length of the sequence, since different searchers perform differently at different sequence lengths.
    *
    * @param theSequence The SequenceMatcher to search for.
    * @return A SequenceSearcher giving good performance for the SequenceMatcher.
    * @throws IllegalArgumentException if the SequenceMatcher is null.
    */
   SequenceSearcher create(SequenceMatcher theSequence);

}
