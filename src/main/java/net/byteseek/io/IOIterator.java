/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.io;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * An iterator used to iterate over some data source which can throw IOExceptions.
 * <p>
 * It follows the same semantics as the standard Java Iterator interface,
 * except that it can throw an IOException on any call.
 * <p>
 * This is motivated by not wanting to disguise an IOException by swallowing it or throwing
 * an unusual RuntimeException which may not be appropriately handled.  If we never
 * had checked exceptions, this wouldn't be an issue.  Since we do, this class allows
 * us to preserve familiar semantics while not disguising the fact of IOExceptions
 * during IO operations.
 * <p>
 * I am open to better design options - this is a compromise between safety and familiarity.
 *
 * @param <E> The type of object returned on a call to next().
 */
public interface IOIterator<E> {

    /**
     * Returns true if there is another element to return on a call to next().
     *
     * @return true if there is another element to return on a call to next().
     * @throws IOException If there was a problem reading the underlying data source.
     */
    boolean hasNext() throws IOException;

    /**
     * Returns the next object from the underlying data source.
     * If there is no next object - hasNext() returns false() - then a NoSuchElementException is thrown.
     *
     * @return the next object from the underlying data source.
     * @throws IOException If there was a problem reading the underlying data source.
     * @throws NoSuchElementException if there is no element to return.
     */
    E next() throws IOException;

    /**
     * Removes the last item returned by a call to next().  It can only have an effect once per call to next().
     *
     * @throws IOException If there was a problem reading the underlying data source.
     * @throws UnsupportedOperationException if this operation is not supported.
     * @throws IllegalStateException If the next() method has not been called, or remove() has already been called for the current item.
     */
    void remove() throws IOException;

}
