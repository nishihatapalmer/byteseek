/*
 * Copyright Matt Palmer 2016, All rights reserved.
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
 */

package net.byteseek.searcher.sequence;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.searcher.Searcher;

import java.io.IOException;

/**
 * TODO: describe interface.
 * Created by matt on 12/04/16.
 */
public interface SequenceSearcher<T> extends Searcher<T> {

    /**
     * Searches bytes forwards provided by a {@link WindowReader} object, from the
     * position given by fromPosition up to toPosition.
     *
     * @param reader
     *            The byte reader giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @param toPosition
     *            The position to search up to.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     * @throws IOException
     */
    long searchSequenceForwards(WindowReader reader, long fromPosition, long toPosition) throws IOException;

    /**
     * Searches bytes forwards provided by a {@link WindowReader} object, from the
     * position given by fromPosition up to the end of the byte reader.
     *
     * @param reader
     *            The byte reader giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     * @throws IOException
     */
    long searchSequenceForwards(WindowReader reader, long fromPosition) throws IOException;

    /**
     * Searches bytes forwards provided by a {@link WindowReader} object, from the
     * start of the {@link WindowReader} to the end, if a match is not found.
     *
     * @param reader
     *            The byte reader giving access to the bytes being searched.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     * @throws IOException
     */
    long searchSequenceForwards(WindowReader reader) throws IOException;

    /**
     * Searches bytes forwards provided by a byte array from the position given
     * by fromPosition up to toPosition.
     *
     * @param bytes
     *            The byte array giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @param toPosition
     *            The position to search up to.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     */
    int searchSequenceForwards(byte[] bytes, int fromPosition, int toPosition);

    /**
     * Searches bytes forwards provided by a byte array from the position given
     * by fromPosition up to the end of the byte array.
     *
     * @param bytes
     *            The byte array giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     */
    int searchSequenceForwards(byte[] bytes, int fromPosition);

    /**
     * Searches bytes forwards provided by a byte array
     *
     * @param bytes
     *            The byte array giving access to the bytes being searched.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     */
    int searchSequenceForwards(byte[] bytes);

    /**
     * Searches bytes backwards provided by a {@link WindowReader} object, from the
     * position given by fromPosition up to toPosition.
     *
     * @param reader
     *            The byte reader giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @param toPosition
     *            The position to search back to.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     * @throws IOException
     */
    long searchSequenceBackwards(WindowReader reader, long fromPosition, long toPosition) throws IOException;

    /**
     * Searches bytes backwards provided by a {@link WindowReader} object, from the
     * position given by fromPosition up to the start of the reader.
     *
     * @param reader
     *            The byte reader giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     * @throws IOException
     */
    long searchSequenceBackwards(WindowReader reader, long fromPosition) throws IOException;

    /**
     * Searches bytes backwards provided by a {@link WindowReader} object, from the
     * end to the start.
     *
     * @param reader
     *            The byte reader giving access to the bytes being searched.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     * @throws IOException
     */
    long searchSequenceBackwards(WindowReader reader) throws IOException;

    /**
     * Searches bytes backwards provided by a byte array, from the position
     * given by fromPosition up to toPosition.
     *
     * @param bytes
     *            The byte array giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @param toPosition
     *            The position to search back to.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     */
    int searchSequenceBackwards(byte[] bytes, int fromPosition, int toPosition);

    /**
     * Searches bytes backwards provided by a byte array, from the position
     * given by fromPosition up to the start of the byte array.
     *
     * @param bytes
     *            The byte array giving access to the bytes being searched.
     * @param fromPosition
     *            The position to search from.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     */
    int searchSequenceBackwards(byte[] bytes, int fromPosition);

    /**
     * Searches a byte array backwards, from the end to the start.
     *
     * @param bytes
     *            The byte array giving access to the bytes being searched.
     * @return The position a match was found at, or a negative number if no
     *         match was found.
     */
    int searchSequenceBackwards(byte[] bytes);
    
}
