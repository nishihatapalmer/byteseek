/*
 * Copyright Matt Palmer 2013, All rights reserved.
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


package net.byteseek.matcher.bytes;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.io.reader.Window;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.sequence.ByteMatcherSequenceMatcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.object.ArgUtils;


/**
 * A TwoByteMatcher is a {@link ByteMatcher} which matches
 * two possible byte values.
 *
 * @author Matt Palmer
 */
public final class TwoByteMatcher extends AbstractByteMatcher {

    private final byte firstByteToMatch;
    private final byte secondByteToMatch;
    
    
    /**
     * Constructs an immutable TwoByteMatcher.
     * 
     * @param firstByteToMatch The first byte to match.
     * @param secondByteToMatch The second byte to match.
     */
    public TwoByteMatcher(final byte firstByteToMatch, final byte secondByteToMatch) {
        this.firstByteToMatch  = firstByteToMatch;
        this.secondByteToMatch = secondByteToMatch;
    }
    
    
    /**
     * Constructs an immutable TwoByteMatcher from hex representations of the bytes.
     * 
     * @param firstHexByte The first byte as a hex string.
     * @param secondHexByte The second byte as a hex string. 
     * @throws IllegalArgumentException if the string is not a valid 2-digit hex byte.
     */
    public TwoByteMatcher(final String firstHexByte, final String secondHexByte) {
        this.firstByteToMatch  = ByteUtils.byteFromHex(firstHexByte);
        this.secondByteToMatch = ByteUtils.byteFromHex(secondHexByte);
    }
    
    
    /**
     * Constructs an immutable TwoByteMatcher from a collection of bytes.
     * The collection must have two bytes in it.
     * 
     * @param twoBytes The collection of bytes to construct from.
     * @throws IllegalArgumentException if the collection of bytes is null, has null elements or
     *         does not have exactly two bytes in it.
     */
    public TwoByteMatcher(final Collection<Byte> twoBytes) {
    	ArgUtils.checkCollectionSizeNoNullElements(twoBytes, 2);
    	if (twoBytes.size() != 2) {
    		throw new IllegalArgumentException("Collection must have two elements");
    	}
    	final Iterator<Byte> byteIterator = twoBytes.iterator();
    	this.firstByteToMatch = byteIterator.next();
    	this.secondByteToMatch = byteIterator.next();
    }


    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the WindowReader passed in is null.
     */
    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException{
        final Window window = reader.getWindow(matchPosition);
        if (window != null) {
        	final byte windowByte = window.getByte(reader.getWindowOffset(matchPosition));
        	return windowByte == firstByteToMatch || windowByte == secondByteToMatch;
        }
        return false;
    }


    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the byte array passed in is null.
     */
    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        if (matchPosition >= 0 && matchPosition < bytes.length) {
        	final byte theByte = bytes[matchPosition];
        	return theByte == firstByteToMatch || theByte == secondByteToMatch;
        }
        return false; 
    }   
    
    
    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if the byte array passed in is null.
     */
    @Override
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition) {
    	final byte theByte = bytes[matchPosition];
    	return theByte == firstByteToMatch || theByte == secondByteToMatch;
    }    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final byte theByte) {
        return theByte == firstByteToMatch || theByte == secondByteToMatch;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getMatchingBytes() {
        return (firstByteToMatch != secondByteToMatch)? new byte[] {firstByteToMatch, secondByteToMatch}
    												  : new byte[] {firstByteToMatch};
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toRegularExpression(final boolean prettyPrint) {
    	return ByteUtils.bytesToString(prettyPrint, getMatchingBytes());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfMatchingBytes() {
        return (firstByteToMatch != secondByteToMatch)? 2 : 1;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override    
    public SequenceMatcher repeat(int numberOfRepeats) {
        ArgUtils.checkPositiveInteger(numberOfRepeats);
        if (numberOfRepeats == 1) {
            return this;
        }
        if (getNumberOfMatchingBytes() == 1) {
        	return new ByteMatcherSequenceMatcher(numberOfRepeats, OneByteMatcher.valueOf(firstByteToMatch));
        }
        return new ByteMatcherSequenceMatcher(numberOfRepeats, this);
    }    
    

    @Override
    public String toString() {
    	return getClass().getSimpleName() + "[first byte:" + firstByteToMatch + 
    			                            " second byte:" + secondByteToMatch + ']';
    }
    
    
}
