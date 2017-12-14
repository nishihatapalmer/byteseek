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

package net.byteseek.io.reader.cache;

import java.io.IOException;

import net.byteseek.io.reader.windows.Window;

/**
 * An AbstractCache implements the {@link net.byteseek.io.reader.cache.WindowCache.WindowObserver} part of a {@link WindowCache},
 * providing subscription, unsubscription and notification services.
 * <p>
 * Observers can receive notifications that a Window is leaving a WindowCache.
 * <p>
 * This is not thread-safe - there is no synchronisation in the list of observers
 * or access to the list.
 *
 * @author Matt Palmer
 */
public abstract class AbstractMemoryCache extends AbstractFreeNotificationCache  {

    @Override
    public int read(final long windowPos, final int offset, final byte[] readInto, int readIntoPos) throws IOException {
        final int bytesToRead = readInto.length - readIntoPos;
        int arrayPos = readIntoPos;
        if (bytesToRead > 0) {

            final int arrayLength = readInto.length;
            long winPos = windowPos;
            int winOffset = offset;
            Window window;
            int bytesToCopy;
            while((window = getWindow(winPos)) != null &&
                  (bytesToCopy = Math.min(arrayLength - arrayPos, window.length() - winOffset)) > 0) {
                System.arraycopy(window.getArray(), winOffset, readInto, arrayPos, bytesToCopy);

                arrayPos += bytesToCopy;
                winPos  += bytesToCopy;
                winOffset = 0;
            }
        }
        return arrayPos - readIntoPos;
    }

}
