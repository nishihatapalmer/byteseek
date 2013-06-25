/**
 * 
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
