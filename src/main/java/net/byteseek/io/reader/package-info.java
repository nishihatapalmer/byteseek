/*
 * Copyright Matt Palmer 2015-16, All rights reserved.
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

/**
 * A package which provides a consistent random-access interface over a variety of
 * input sources including Files, InputStreams, SeekableByteChannels, Strings and byte arrays.
 * <p>
 * The input source is chunked on demand into windows over the data, which each encapsulate a byte array.
 * This allows clients to access the arrays directly, and for the windows
 * to be cached using a variety of caching strategies defined in the cache sub-package.
 * <p>
 * Input sources which are already support random access just fetch the appropriate window on demand.
 * Input sources which are not random access will directly return a Window which has already been read, or will
 * read forward (caching as they go) to obtain a new Window which has not yet been read.  Care must be taken to
 * select cache strategies that will be able to return any Window the application requests again.
 * <p>
 * There are two types of Window currently defined.  HardWindows store a hard reference
 * to the underlying byte array.  SoftWindows use a SoftReference to the array, which allows
 * the garbage collector to re-use the array memory if it is not currently in use.  For
 * applications which are processing byte sources quickly, SoftWindows may help to prevent
 * OutOfMemoryErrors.  Otherwise, they simply add IO overhead as windows may need to be re-read.
 * <p>
 * In addition, the ReaderInputStream adapts any WindowReader into an InputStream, to allow the
 * cached windows to be used with other classes which require input streams.
 */
package net.byteseek.io.reader;