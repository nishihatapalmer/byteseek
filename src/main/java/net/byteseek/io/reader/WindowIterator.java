/*
 * Copyright Matt Palmer 2017-21, All rights reserved.
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
import net.byteseek.utils.ArgUtils;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * An IOIterator of {@link net.byteseek.io.reader.windows.Window}s over a {@link WindowReader}.
 */
public final class WindowIterator implements IOIterator<Window> {

    private final WindowReader reader;
    private Window nextWindow;
    private long position;
    private long toPosition;

    /**
     * Constructs a WindowIterator given a WindowReader to iterate over,
     * starting at position 0 and ending at the last Window.
     *
     * @param reader The WIndowReader to iterate over.
     * @throws IllegalArgumentException if the reader supplied is null.
     */
    public WindowIterator(final WindowReader reader) {
        this(reader, 0, Long.MAX_VALUE);
    }

    /**
     * Constructs a WindowIterator given a WindowReader to iterate over
     * and a position to start from.
     *
     * @param reader The WIndowReader to iterate over.
     * @param fromPosition The first position to obtain a window for.
     * @throws IllegalArgumentException if the reader supplied is null.
     */
    public WindowIterator(final WindowReader reader, final long fromPosition) {
        this(reader, fromPosition, Long.MAX_VALUE);
    }

    //TODO: should iterator endPosition be exclusive?

    /**
     * Constructs a WindowIterator given a WindowReader to iterate over,
     * a position to start from, and a position to finish on,
     * or the end of the WindowReader data is found.
     *
     * @param reader The WindowReader to iterate over.
     * @param fromPosition The first position to obtain a window for.
     * @param toPosition The last position (inclusive) to obtain a window for (assuming there are windows at this position).
     * @throws IllegalArgumentException if the reader supplied is null or the toPosition is less than the fromPosition.
     */
    public WindowIterator(final WindowReader reader, final long fromPosition, final long toPosition) {
        ArgUtils.checkNullObject(reader, "reader");
        ArgUtils.checkAtLeast(toPosition, fromPosition, "toPosition must not be less than the fromPosition");
        this.reader = reader;
        this.position = fromPosition;
        this.toPosition = toPosition;
    }

    @Override
    public boolean hasNext() throws IOException {
        if (nextWindow == null && position <= toPosition) {
            nextWindow = reader.getWindow(position);
        }
        return nextWindow != null;
    }

    @Override
    public Window next() throws IOException {
        if (hasNext()) {
            final Window theWindow = nextWindow;
            nextWindow = null;
            position = theWindow.getNextWindowPosition();
            return theWindow;
        }
        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Note</b>
     * This implementation always throws UnsupportedOperationException.
     * It is not possible to remove a Window from a WindowReader.
     *
     * @throws UnsupportedOperationException - always throws this exception.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Cannot remove a window from a reader.");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(reader: " + reader + " position: " + position + " endIndex" + toPosition + ')';
    }
}