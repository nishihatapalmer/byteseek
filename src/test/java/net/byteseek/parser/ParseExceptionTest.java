/*
 * Copyright Matt Palmer 2013, All rights reserved.
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
package net.byteseek.parser;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author matt
 *
 */
public class ParseExceptionTest {

	@Test
	public final void testMessageConstructor() {
		ParseException pe = new ParseException("A message");
		assertEquals("Message is correct", "A message", pe.getMessage());
		assertNull("Cause is null", pe.getCause());
	}
	
	@Test
	public final void testCauseConstructor() {
		Throwable cause = new Exception();
		ParseException pe = new ParseException(cause);
		assertEquals("Cause is correct", cause, pe.getCause());
		assertEquals("Message is java.lang.Exception", "java.lang.Exception", pe.getMessage());
	}
	
	@Test
	public final void testMessageAndCauseConstructor() {
		Throwable cause = new Exception();
		ParseException pe = new ParseException("A message", cause);
		assertEquals("Cause is correct", cause, pe.getCause());
		assertEquals("Message is correct", "A message", pe.getMessage());
	}


}
