/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher;

import net.domesdaybook.reader.ByteReader;

/**
 * An interface for classes that can match bytes from a given position.
 * 
 * @author Matt Palmer
 */
public interface Matcher {

    /**
     *
     * @param reader The {@link ByteReader} to read from.
     * @param matchPosition The position to try to match at.
     * @return Whether there is a match at the given position.
     */
    public boolean matches(final ByteReader reader, final long matchPosition);
}
