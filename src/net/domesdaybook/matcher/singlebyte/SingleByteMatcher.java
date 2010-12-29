/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.matcher.Matcher;

/**
 *
 * @author matt
 */
public interface SingleByteMatcher extends Matcher {

    /*
     * @return whether a single byte matches the byte matcher.
     */
    public boolean matches(byte theByte);

    
    /**
     * @return an array of all the bytes that this byte matcher could match.
     */
    public byte[] getMatchingBytes();


    /**
     *
     * @return the number of bytes this byte matcher will match.
     */
    public int getNumberOfMatchingBytes();
    

    /**
     *
     * @return a string representation of a regular expression for this matcher.
     */
    public String toRegularExpression(final boolean prettyPrint);

}
