package net.byteseek.io.reader;

/**
 * Created by matt on 02/10/15.
 */
public interface Window {

	byte getByte(int position);

	byte[] getArray();

	long getWindowPosition();

	long getWindowEndPosition();

	long getNextWindowPosition();

	int length();
}
