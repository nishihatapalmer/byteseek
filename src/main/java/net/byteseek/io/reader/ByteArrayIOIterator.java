/*
 * Copyright Matt Palmer 2021, All rights reserved.
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
package net.byteseek.io.reader;

import net.byteseek.io.IOIterator;
import net.byteseek.io.reader.windows.Window;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * An IOIterator that returns the data in a WindowReader as a sequence of byte arrays.
 * If not specified, all data in the WindowReader is iterated, but a start position and end position can be specified.
 * The iterator may return the actual byte arrays cached by the WindowReader, or it may create new ones to satisfy
 * the requirement that only the data specified is returned (e.g. if a start or end position is in the middle of an
 * existing array, or the existing backing arrays are larger than the actual data held within them.
 */
public class ByteArrayIOIterator implements IOIterator<byte[]> {

    private final WindowReader reader;
    private final WindowIterator windowIterator;
    private long position;
    private long toPosition;

    /**
     * Constructs a ByteArrayIOIterator given a WindowReader which will iterate over all the data in the WindowReader
     * as a sequence of one or more byte arrays.
     *
     * @param source The WindowReader to obtain the byte array data for.
     * @throws IllegalArgumentException if the source is null.
     */
    public ByteArrayIOIterator(final WindowReader source) {
        this(source, 0, Long.MAX_VALUE);
    }

    /**
     * Constructs a ByteArrayIOIterator given a WindowReader which will iterate over some of the data in the WindowReader
     * as a sequence of one or more byte arrays from the startIndex provided.
     *
     * @param source The WindowReader to obtain the byte array data for.
     * @param fromPosition The first position for which you want data, zero being the start.
     * @throws IllegalArgumentException if the source is null.
     */
    public ByteArrayIOIterator(final WindowReader source, final long fromPosition) {
        this(source, fromPosition, Long.MAX_VALUE);
    }

    /**
     * Constructs a ByteArrayIOIterator given a WindowReader which will iterate over some of the data in the WindowReader
     * as a sequence of one or more byte arrays, given a start position and an end position.
     *
     * @param source The WindowReader to obtain the byte array data for.
     * @param fromPosition The first position for which you want data, zero being the start.
     * @param toPosition The last position (inclusive) for which you want data.
     * @throws IllegalArgumentException if the source is null.
     */
    public ByteArrayIOIterator(final WindowReader source, final long fromPosition, final long toPosition) {
        this.reader = source;
        this.windowIterator = new WindowIterator(source, fromPosition, toPosition);
        this.position = fromPosition;
        this.toPosition = toPosition;
    }

    @Override
    public boolean hasNext() throws IOException {
        return windowIterator.hasNext();
    }

    @Override
    public byte[] next() throws IOException {
        if (hasNext()) {
            final Window window = windowIterator.next();
            final byte[] result = canUseWindowArray(window) ? window.getArray() : copyPartialArray(window);
            position = window.getNextWindowPosition();
            return result;
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "( position:" + position + ", to position:" + toPosition +
                ", iterator:" + windowIterator + ')';
    }

    /**
     * If the byte array backing a Window gives us all the data we want and no more, we can just use it directly.
     *
     * @param window The Window to check.
     * @return True if the array backing the window can be used as-is, without copying to another array.
     * @throws IOException If there's a problem reading data.
     */
    private boolean canUseWindowArray(Window window) throws IOException {
        return windowStartsAtCurrentPosition(window) && needEntireWindow(window) && arrayNotLongerThanWindow(window);
    }

    /**
     * A Window that starts at our current position means we need all the data at the start of the Window.
     * If a Window starts before our current position (e.g. if we have a start position in the middle of a Window)
     * then we would need to discard the first part of the Window data.
     *
     * @param window The Window to check.
     * @return True if the start of the current window is also the position we are currently at.
     */
    private boolean windowStartsAtCurrentPosition(Window window) {
        return position == window.getWindowPosition();
    }

    /**
     * If all the data in our Window is before the end position, we need all of it.
     *
     * @param window The Window to check.
     * @return True if all the data in this Window is required.
     */
    private boolean needEntireWindow(Window window) {
        return window.getNextWindowPosition() <= toPosition;
    }

    /**
     * A Window can use an array larger than the actual data in it.
     * A Window has a length property, which defines the length of the data.  The backing array is allowed
     * to be larger than that (although in practice, most arrays are the same size as the data they contain,
     * except for the last window).
     *
     * @param window The Window to check.
     * @return True if the array backing the Window is not larger than the data recorded in it.
     * @throws IOException If there's a problem reading data.
     */
    private boolean arrayNotLongerThanWindow(Window window) throws IOException {
        return window.getArray().length <= window.length();
    }

    /**
     * Makes a copy of an array into another one sized to only hold the copy data.
     * We copy from the position we are at (which may be offset from the start of a Window),
     * and copy as many bytes from the remaining window as possible as long as we don't exceed the
     * end position.
     *
     * @param window The Window to copy data from.
     * @return A copy of the data in the Window in a new byte array.
     * @throws IOException If there's a problem reading data.
     */
    private byte[] copyPartialArray(Window window) throws IOException {
        final int startArrayPosition = reader.getWindowOffset(position);
        final int endArrayPosition = needEntireWindow(window) ? window.length() - 1 : reader.getWindowOffset(toPosition);
        return Arrays.copyOfRange(window.getArray(), startArrayPosition, endArrayPosition + 1);
    }

}
