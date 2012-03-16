/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
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


package net.domesdaybook.matcher.bytes;

import java.io.IOException;
import net.domesdaybook.util.bytes.ByteUtilities;
import java.util.List;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;

/**
 * A {@link SingleByteMatcher} which matches a byte which
 * shares any of its bits with a bitmask.
 * 
 * @author Matt Palmer
 */
public final class AnyBitmaskMatcher extends InvertibleMatcher {

    final byte mBitMaskValue;


     /**
     * Constructs an immutable AnyBitmaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     */
    public AnyBitmaskMatcher(final byte bitMaskValue) {
        super(false);
        mBitMaskValue = bitMaskValue;
    }

    /**
     * Constructs an immutable AnyBitmaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match any of its bits against.
     * @param inverted Whether the result of a match should be inverted.
     */
    public AnyBitmaskMatcher(final byte bitMaskValue, boolean inverted) {
        super(inverted);
        mBitMaskValue = bitMaskValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchFrom) throws IOException {
        final Window window = reader.getWindow(matchFrom);
        return window == null? false
               : ((window.getByte(reader.getWindowOffset(matchFrom)) & mBitMaskValue) != 0) ^ inverted;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchFrom) {
        return (matchFrom >= 0 && matchFrom < bytes.length) &&
                (((bytes[matchFrom] & mBitMaskValue) != 0) ^ inverted);
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return ((theByte & mBitMaskValue) != 0) ^ inverted;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String wrapper = inverted? "[^ ~%02x]" : "~%02x";
        final String regEx = String.format(wrapper, (int) 0xFF & mBitMaskValue);
        return prettyPrint ? " " + regEx + " " : regEx;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        final List<Byte> bytes = ByteUtilities.getBytesMatchingAnyBitMask(mBitMaskValue);
        return ByteUtilities.toArray(bytes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return ByteUtilities.countBytesMatchingAnyBit(mBitMaskValue);
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchFrom) {
        return ((bytes[matchFrom] & mBitMaskValue) != 0) ^ inverted;
    }
    
    



}
