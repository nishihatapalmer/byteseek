/*
 * Copyright Matt Palmer 2012-13, All rights reserved.
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

import net.byteseek.compiler.Optimiser;
import net.byteseek.matcher.bytes.AnyByteMatcher;
import net.byteseek.matcher.bytes.ByteMatcher;
import net.byteseek.matcher.sequence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Optimises a sequence of ByteMatchers contained in a SequenceSequenceMatcher.
 * <p>
 * This optimises assumes that all the sequences contained in the sequence to optimise
 * currently have a length of one - they are all ByteMatchers.  The optimisation
 * consists of finding more appropriate containers for these matchers than a general-
 * purpose SequenceSequenceMatcher, or to return a matcher directly if no additional
 * container is required.
 * <p>
 * This optimiser will not check for deeply nested sequence matchers.  It assumes a pretty
 * flat structure of byte matchers contained in a single SequenceSequenceMatcher.
 */
public class SequenceMatcherOptimiser implements Optimiser<SequenceMatcher> {

	public static final Optimiser<SequenceMatcher> SEQUENCE_OPTIMISER = new SequenceMatcherOptimiser();

	// The length up to which it is not worth optimising the sub-components and placing them in a SequenceSequenceMatcher.
	private static final int SIMPLE_MATCHER_CUT_OFF = 5;

	// The percentage of the length of sub-components which can be optimised against the total length of the matcher,
	// beyond which we attempt to optimise the sub-components.
	private static final int SUB_COMPONENT_OPTIMISE_PERCENTAGE = 40;

	@Override
	public SequenceMatcher optimise(final SequenceMatcher toOptimise) {

		final int length = toOptimise.length();

		// If the length of the sequence is just one, return the matcher inside the sequence directly.
		if (length == 1) {
			return toOptimise.getMatcherForPosition(0);
		}

		// Find the total consecutive matchers which only match a single byte (values = 1)
		final int consecutiveOnes = countConsecutiveMatchersWithNumBytes(toOptimise, 1);

		// If the longest run of matchers matching a single byte is equal to the length of the
		// sequence to optimise, we can replace this with a single byte array matcher.
		if (consecutiveOnes == length) {
			return new ByteSequenceMatcher(toOptimise);
		}

		// Find the total consecutive matchers which match all bytes (values = 256)
		final int consecutiveAny = countConsecutiveMatchersWithNumBytes(toOptimise, 256);

		// If all the matchers in the sequence match all bytes, then return a fixed gap matcher:
		if (consecutiveAny == length) {
			return new FixedGapMatcher(length);
		}

		// What remains contains a mixture of byte matchers matching different numbers of bytes.
		// For a short matcher, it is not worth optimising this into a mixture of matchers with
		// different lengths, placed into a generic SequenceSequenceMatcher.  Just use a ByteMatcherSequenceMatcher.
		if (length <= SIMPLE_MATCHER_CUT_OFF) {
			return new ByteMatcherSequenceMatcher(toOptimise);
		}

		// Our sequence may now contain potential arrays or fixed gaps as sub components of a wider sequence.
		// We optimise those subsequences, placing them all into a SequenceSequenceMatcher
		// if the proportion of those subsequences is high enough to warrant the overhead of the SequenceSequenceMatcher.
		final int percentageOfRuns = (consecutiveOnes + consecutiveAny) * 100 / length;
		if (percentageOfRuns >= SUB_COMPONENT_OPTIMISE_PERCENTAGE) {
			return optimiseSubComponents(toOptimise);
		}

    	// Otherwise, just convert the sequenceSequenceMatcher into a ByteMatcherSequenceMatcher,
		// as the lowest common denominator that can handle a mixture of byte value matching with no
		// further sub-component optimisation.
    	return new ByteMatcherSequenceMatcher(toOptimise);
	}


	private SequenceMatcher optimiseSubComponents(SequenceMatcher toOptimise) {
		final int length = toOptimise.length();
		final List<SequenceMatcher> components = new ArrayList<SequenceMatcher>();

		int currentOneRun = 0;
		int currentAnyRun = 0;

		for (int matcherIndex = 0; matcherIndex < length; matcherIndex++) {
			final ByteMatcher byteMatcher = toOptimise.getMatcherForPosition(matcherIndex);
			final int numMatchingValues = byteMatcher.getNumberOfMatchingBytes();

			if (numMatchingValues == 1) {
			    if (currentAnyRun == 1) {
					components.add(AnyByteMatcher.ANY_BYTE_MATCHER);
				} else if (currentAnyRun > 1) {
				    components.add(new FixedGapMatcher(currentAnyRun));
				}
				currentAnyRun = 0;
				currentOneRun++;
			} else if (numMatchingValues == 256) {
				if (currentOneRun == 1) {
					components.add(toOptimise.getMatcherForPosition(matcherIndex - 1));
				} else if (currentOneRun > 1) {
					final int startIndex = matcherIndex - currentOneRun;
					final int endIndex = startIndex + currentOneRun;
					components.add(new ByteSequenceMatcher(toOptimise.subsequence(startIndex, endIndex)));
				}
				currentOneRun = 0;
				currentAnyRun++;
			} else {
				if (currentAnyRun == 1) {
					components.add(AnyByteMatcher.ANY_BYTE_MATCHER);
				} else if (currentAnyRun > 1) {
					components.add(new FixedGapMatcher(currentAnyRun));
				} else if (currentOneRun == 1) {
					components.add(toOptimise.getMatcherForPosition(matcherIndex - 1));
				} else if (currentOneRun > 1) {
					final int startIndex = matcherIndex - currentOneRun;
					final int endIndex = startIndex + currentOneRun;
					components.add(new ByteSequenceMatcher(toOptimise.subsequence(startIndex, endIndex)));
				}
				currentAnyRun = 0;
				currentOneRun = 0;
				components.add(byteMatcher);
			}
		}
		if (currentAnyRun == 1) {
			components.add(AnyByteMatcher.ANY_BYTE_MATCHER);
		} else if (currentAnyRun > 1) {
			components.add(new FixedGapMatcher(currentAnyRun));
		} else if (currentOneRun == 1) {
			components.add(toOptimise.getMatcherForPosition(length - 1 ));
		} else if (currentOneRun > 1) {
			final int startIndex = length - currentOneRun;
			final int endIndex = startIndex + currentOneRun;
			components.add(new ByteSequenceMatcher(toOptimise.subsequence(startIndex, endIndex)));
		}
		return new SequenceSequenceMatcher(components);
	}


	private int countConsecutiveMatchersWithNumBytes(final SequenceMatcher toAnalyse, final int valuesToFind) {
		final int length = toAnalyse.length();

		int totalConsecutive = 0;
		int currentRun = 0;
		for (int matcherIndex = 0; matcherIndex < length; matcherIndex++) {
			final int numMatchingValues = toAnalyse.getMatcherForPosition(matcherIndex).getNumberOfMatchingBytes();
			if (numMatchingValues == valuesToFind) {
				currentRun++;
				if (currentRun == 2) {
					totalConsecutive = 2;
				} else if (currentRun > 2) {
					totalConsecutive++;
				}
			} else {
				currentRun = 0;
			}
		}
		return totalConsecutive;
	}

}
