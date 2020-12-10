/*
 * Copyright Matt Palmer 2020, All rights reserved.
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
 * Byteseek matches and searches for byte patterns using efficient algorithms.
 * It can currently match or search for any fixed length byte pattern,
 * where any position can match any arbitrary set of bytes.
 * <p>
 * {@link net.byteseek.Pattern} is the main class in byteseek.  You can instantiate
 * a Pattern using a byteseek regular expression, or with a different syntax if you supply
 * a parser for it.
 * <p>
 * A Pattern can be efficiently matched or searched in byte arrays,
 * and in any other data source by implementing the WindowReader interface.
 * Byteseek provides implementations for Files, InputStreams and SeekableByteChannels.
 * <p>>
 * All WindowReaders can use a flexible set of caching strategies to ensure good
 * performance on the type of data being matched or searched.  These include two level caches,
 * least recently used, least recently added, top and tail, temporary file, write through and write around caches.
 * If you don't specify a cache, a reasonable default will be chosen.
 */
package net.byteseek;