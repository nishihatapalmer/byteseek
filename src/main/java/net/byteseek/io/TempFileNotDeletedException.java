package net.byteseek.io;

import java.io.IOException;

public class TempFileNotDeletedException extends IOException {

    /**
     * Constructs a TempFileNotDeletedException from a descriptive message and a Throwable cause.
     *
     * @param message The message to include with the exception.
     * @param cause   The Throwable which caused this exception to be thrown.
     */
    public TempFileNotDeletedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
