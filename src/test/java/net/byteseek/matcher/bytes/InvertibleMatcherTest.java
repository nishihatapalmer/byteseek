/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.matcher.bytes;
/*
 * Copyright Matt Palmer 2015-2016, All rights reserved.
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

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.bytes.InvertibleMatcher;

/**
 *
 * @author matt
 */
public class InvertibleMatcherTest {

	/**
	 * Test of isInverted method, of abstract class InvertibleMatcher.
	 */
	@Test
	public void testIsInverted() {
		InvertibleMatcher matcher = new InvertibleMatcherImpl(InvertibleMatcher.INVERTED);
		assertEquals(true, matcher.isInverted());

		matcher = new InvertibleMatcherImpl(InvertibleMatcher.NOT_INVERTED);
		assertEquals(false, matcher.isInverted());
	}

	/**
	 * 
	 */
	public static class InvertibleMatcherImpl extends InvertibleMatcher {

		public InvertibleMatcherImpl(boolean inverted) {
			super(inverted);
		}

		@Override
		public boolean matches(byte theByte) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public byte[] getMatchingBytes() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public int getNumberOfMatchingBytes() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public String toRegularExpression(boolean prettyPrint) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean matches(WindowReader reader, long matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean matches(byte[] bytes, int matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public boolean matchesNoBoundsCheck(WindowReader reader, long matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean matchesNoBoundsCheck(byte[] bytes, int matchPosition) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Iterator<ByteMatcher> iterator() {
			return null;
		}
	}

}