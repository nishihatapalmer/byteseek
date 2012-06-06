/*
 * Copyright Matt Palmer 2009-2012, All rights reserved.
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


package net.domesdaybook.matcher.bytes;

import java.io.IOException;
import java.util.List;
import net.domesdaybook.reader.Reader;
import net.domesdaybook.reader.Window;
import net.domesdaybook.util.bytes.ByteUtilities;


/**
 * A {@link ByteMatcher} which matches a byte which
 * shares all of its set bits with a bitmask (or shares none of them if the
 * results should be inverted).
 *
 * @author Matt Palmer
 */
public final class AllBitmaskMatcher extends InvertibleMatcher {

    final byte mBitMaskValue;

    
    /**
     * Constructs an immutable AllBitmaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match all of its bits against.
     */
    public AllBitmaskMatcher(final byte bitMaskValue) {
        super(false);
        mBitMaskValue = bitMaskValue;
    }

    
    /**
     * Constructs an immutable AllBitmaskMatcher.
     *
     * @param bitMaskValue The bitmaskValue to match all of its bits against.
     * @param inverted Whether the result of matching the bitmask should be inverted.
     */
    public AllBitmaskMatcher(final byte bitMaskValue, final boolean inverted) {
        super(inverted);
        mBitMaskValue = bitMaskValue;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final Reader reader, final long matchPosition) throws IOException {
        final byte localbitmask = mBitMaskValue;
        final Window window = reader.getWindow(matchPosition);
        return window == null? false
               : ((window.getByte(reader.getWindowOffset(matchPosition)) & localbitmask) == localbitmask) ^ inverted;
    }

 
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        final byte localbitmask = mBitMaskValue;
        return (matchPosition >= 0 && matchPosition < bytes.length) &&
               (((bytes[matchPosition] & localbitmask) == localbitmask) ^ inverted);
    }    
    

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        final byte localbitmask = mBitMaskValue;
        return ((theByte & localbitmask ) == localbitmask) ^ inverted;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
        final String wrapper = inverted? "[^ &%02x]" : "&%02x";
        final String regEx = String.format(wrapper, 0xFF & mBitMaskValue);
        return prettyPrint ? ' ' + regEx + ' ' : regEx;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        final List<Byte> bytes = inverted?
                ByteUtilities.getBytesNotMatchingAllBitMask(mBitMaskValue) :
                ByteUtilities.getBytesMatchingAllBitMask(mBitMaskValue);
        return ByteUtilities.toArray(bytes);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return inverted? 256 - ByteUtilities.countBytesMatchingAllBits(mBitMaskValue) :
                               ByteUtilities.countBytesMatchingAllBits(mBitMaskValue);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
        final byte localbitmask = mBitMaskValue;
        return ((bytes[matchPosition] & localbitmask) == localbitmask) ^ inverted;
    }

    
}
