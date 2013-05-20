/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
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
