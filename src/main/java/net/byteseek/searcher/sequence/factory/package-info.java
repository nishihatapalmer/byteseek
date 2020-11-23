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
 * The FastSearcherFactory gives good performance for most sorts of data and pattern.
 * <p>
 * When the sequence length is just one, there are searchers that will always outperform any other.
 * The AbstractSequenceFactory implements all of those possibilities, leaving subclasses to use other
 * strategies for sequences with a length greater than one.
 * <p>
 * For short sequences it is hard to beat the ShiftOr searchers.  Even though these examine every byte
 * and do not attempt to skip, they use bit-parallelism to verify a match (essentially for free), they
 * are simple, they have excellent cache locality, and pattern complexity has almost no effect on them.
 * <p>
 * Longer sequences benefit from approaches which allow them to skip ahead in the search data, based on
 * various ways of indexing the patterns they are searching for.  This skip is always limited by the length
 * of the pattern to be searched for.  They can never skip more than this ahead, or they risk missing
 * a match.  Therefore, longer sequences allow for longer skips ahead.
 * <p>
 * Complex patterns which match more than one byte in various positions can negatively index-based searchers,
 * even if the pattern is long,  This depends on where the complexity is and the algorithm being used. For Horspool
 * searchers and variants, and SignedHash searchers, if lots of bytes are being matched at the end of a pattern
 * (if searching forwards), or at the start of it (if searching backwards), then this seriously reduces the shifts
 * it can make.
 * If lots of multiple byte matches are next to each other in a pattern, it can create too many permutations of
 * possible matching byte sequences, which either limits the size of the shifts a searcher can make,
 * or in the worst cases can overwhelm the algorithm and cause it to perform extremely poorly (e.g. QGramFiltering).
 * <p>
 * There is a cut-over between simple searchers that look at everything and more complex searchers that
 * attempt to skip over data.  From theory you would expect the cleverer searchers to outpeform the simpler
 * ones much sooner - but the reality of caching, pipelining, branch-prediction and other optimisations in
 * modern hardware means that simpler can often work much better than you might expect.
 * <p>
 * So, there is not any single searcher which is better than all others in all circumstances.
 * Some generally perform very well but have expensive worst cases (e.g. QGramFiltering)
 * Some generally perform well, but are not the fastest for all searches (e.g. SignedHash and Horspool variants)
 * Some are good when the data has high entropy (e.g. QGramFiltering),
 * and some are good when the entropy is lower (e.g. DNA using SignedHash).
 * <p>
 * All the Searchers in byteseek have defences against too much complexity in the patterns they encounter
 * and will select parameters that give reasonable performance without incurring too much processing or
 * storage costs.  Even so, another search algorithm could still outperform it given what we know about
 * the data and the pattern, if it had been selected in the first place - which is what SearcherFactories
 * are for.
 */
package net.byteseek.searcher.sequence.factory;