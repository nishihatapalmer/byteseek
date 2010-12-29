/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.matcher;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public interface Matcher {

    /* matches an entire sequence of bytes or not.
     * @returns whether the byte matcher matched a sequence of bytes or not.
    */
    public boolean matches(final ByteReader reader, final long matchFrom);
}
