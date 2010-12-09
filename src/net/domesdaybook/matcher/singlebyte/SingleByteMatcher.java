/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.matcher.singlebyte;

/**
 *
 * @author matt
 */
public interface SingleByteMatcher {

    /*
     * @return whether a single byte matches the byte matcher.
     */
    public boolean matchesByte(byte theByte);

    /**
     * @return an array of all the bytes that this byte matcher could match.
     */
    public byte[] getMatchingBytes();


    /**
     *
     * @return a string representation of a regular expression for this matcher.
     */
    public String toRegularExpression(final boolean prettyPrint);

}
