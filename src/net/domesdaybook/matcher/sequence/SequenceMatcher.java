/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher.sequence;

import net.domesdaybook.matcher.Matcher;
import net.domesdaybook.matcher.singlebyte.SingleByteMatcher;
import net.domesdaybook.reader.Reader;

/**
 * An extension to the {@link Matcher} interface to support sequences.
 *
 * @author Matt Palmer
 */
 public interface SequenceMatcher extends Matcher {

    /**
     * Returns a {@link SingleByteMatcher} which matches all the bytes at
     * the requested position in the sequence.
     *
     * @param position The position in the byte matcher to return a dedicated byte matcher for.
     * @return A SingleByteMatcher for the position in the sequence provided.
     */
    public SingleByteMatcher getByteMatcherForPosition(final int position);

    
    /**
     * Returns whether there is a match or not at the given position in a Reader.
     * <p/>
     * It does not perform any bounds checking, so an exception can be thrown,
     * or results may be undefined if matching is outside the bounds of the array,
     * depending on the implementation of the Reader object.
     * <p/>
     * It may also throw a ByteReaderException or other exception if a serious problem
     * occurred, depending on the implementation of the Reader.
     * 
     * @param reader The {@link Reader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */    
    public boolean matchesNoBoundsCheck(final Reader reader, final long matchPosition);
    
    
    
    /**
     * Returns whether there is a match or not at the given position in a byte array.
     * <p/>
     * It does not perform any bounds checking, so an IndexOutOfBoundsException
     * can be thrown by this method if matching is outside the bounds of the array.
     * 
     * @param bytes An array of bytes to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matchesNoBoundsCheck(final byte[] bytes, final int matchPosition);    
    
    
    /**
     * Gets the length of the matching sequence.
     *
     * @return Returns the length of a matching byte sequence.
     */
    public int length();

    
    /**
     * Returns a reversed SequenceMatcher
     * 
     * @return A SequenceMatcher which matches the reverse sequence.
     */
    public SequenceMatcher reverse();
    
    
    /**
     * Returns a regular expression representation of the matching sequence.
     * 
     * @param prettyPrint whether to pretty print the regular expression with spacing.
     * @return A string containing a regular expression of the byte matcher.
     */
    public String toRegularExpression(final boolean prettyPrint);

}
