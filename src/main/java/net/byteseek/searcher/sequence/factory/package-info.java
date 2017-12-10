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
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * A package containing factories for SequenceSearchers.  There is rarely a single searcher that
 * works well for all sequences.
 * <p>
 * When the sequence length is just one, there are searchers that will always outperform any other.
 * The AbstractSequenceFactory implements all of those possibilities, leaving subclasses to use other
 * strategies for sequences with a length greater than one.
 * <p>
 * For short sequences it is hard to beat the ShiftOr searchers.  Even though these examine every byte
 * and do not attempt to skip, they use bit-parallelism to verify a match (essentially for free), they
 * are simple, they have good cache locality, and pattern complexity has no effect on them.
 * <p>
 * Longer sequences benefit from approaches which allow them to skip ahead in the search data, based on
 * various ways of indexing the patterns they are searching for.  This skip is always limited by the maximum
 * length of the pattern to be searched for.  They can never skip more than this ahead, or they risk missing
 * a match.  Therefore, longer sequences allow for longer skips ahead.
 * <p>
 * However, there is not any single searcher for longer sequences which is better than all others in all
 * circumstances.  Some perform very well but have expensive worst cases.  For example, in QGramFiltering where
 * either the index table fills up, or the pattern and data have a very low alphabet (e.g. DNA), this algorithm
 * performs extremely poorly - far worse than just doing a naive search - but is very fast otherwise.
 * Some generally perform well, but may not be the fastest for all searches (e.g. SignedHash, SignedHorpsool, UnrolledHorspool)
 * <p>
 * When patterns can match more than one byte in some positions - a gap in the pattern, or
 * because a wildcard, set, bitmask or other kind of multiple-matcher is involved, these
 * can also degrade searcher performance, depending on where in the pattern the complexity
 * is situated and how complex it is.  All the searchers in byteseek have defences against
 * too much complexity in the patterns they encounter - and will try to select the parameters
 * that they can without incurring unnecessary processing or storage, but another algorithm
 * could still easily outperform it.
 */
package net.byteseek.searcher.sequence.factory;