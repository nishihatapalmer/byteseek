package net.byteseek.io.reader.windows;

import java.io.IOException;

/**
 * Created by matt on 02/10/15.
 */
public interface Window {

	byte getByte(int position) throws IOException;

	byte[] getArray() throws IOException;

	long getWindowPosition();

	long getWindowEndPosition();

	long getNextWindowPosition();

	int length();
}
