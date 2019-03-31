package net.byteseek.io.reader.windows;

import org.junit.Test;

import static org.junit.Assert.*;

public class WindowMissingExceptionTest {

    @Test
    public void testConstructMessageOnly() {
        String message = "the message";
        WindowMissingException exception = new WindowMissingException(message);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }


    @Test
    public void testConstructThrowableOnly() {
        String message = "the message";
        Throwable cause = new RuntimeException("cause");
        WindowMissingException exception = new WindowMissingException(cause);
        assertNotEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    public void testConstructThrowableMessage() {
        String message = "the message";
        Throwable cause = new RuntimeException("cause");
        WindowMissingException exception = new WindowMissingException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}