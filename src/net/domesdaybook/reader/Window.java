/*
 * Copyright Matt Palmer 2011, All rights reserved.
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
 * 
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public final class Window {
    
    private final byte[] bytes;
    private final long windowPosition;
    private final int length;
    
    /**
     * Constructs a Window using the byte array provided.
=    * 
     * @param bytes  The byte array to wrap.
     * @param windowPosition The position at which the Window starts.
     * @param length  An ending position of a slice of the array.
     */
    public Window(final byte[] bytes, final long windowPosition, final int length) {
        if (bytes == null) {
            throw new IllegalArgumentException("Null byte array passed in to Array.");
        }
        this.bytes = bytes;  
        this.windowPosition = windowPosition;
        this.length = length;
    }
    
    
    
    /**
     * 
     * @param position
     * @return
     */
    public byte getByte(final int position) {
        return bytes[position];
    }

    
    /**
     * Returns the array of bytes backing this Window.  It does not clone
     * or return a copy of the bytes, as the entire goal is performance.
     * Hence, it is possible to abuse this.  Clients should not alter the
     * array returned by this method.
     * 
     * @return
     */
    public byte[] getArray() {
        return bytes; 
    }
    
    
    /**
     * 
     * @return
     */
    public long getWindowPosition() {
        return windowPosition;
    }
    
    
    /**
     * 
     * @return
     */
    public int length() {
        return length;
    }
}
