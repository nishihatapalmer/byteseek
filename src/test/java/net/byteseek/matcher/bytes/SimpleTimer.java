/*
 * Copyright Matt Palmer 2009-2016, All rights reserved.
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

package net.byteseek.matcher.bytes;

import net.byteseek.matcher.bytes.ByteMatcher;


/**
 *
 * @author matt
 */
public class SimpleTimer {

	private static final String	timingResults	= "%d ms for total test\t%d average nanos per test\t%s";
	private static final int	TIMES_TO_TEST	= 1000;

	/**
	 * 
	 * @param description
	 * @param matcher
	 */
	public static void timeMatcher(String description, ByteMatcher matcher) {

		long start = System.nanoTime();

		for (int i = 0; i < TIMES_TO_TEST; i++) {
			for (byte value = Byte.MIN_VALUE; value < Byte.MAX_VALUE; value++) {
				matcher.matches(value);
			}
		}

		long stop = System.nanoTime();

		long averageNanosPerMatch = (stop - start) / (TIMES_TO_TEST * 256);

		System.out.println(String.format(timingResults, (stop - start) / 1000000,
				averageNanosPerMatch, description));
	}

}
