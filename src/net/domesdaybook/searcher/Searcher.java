/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public interface Searcher {

    public long NOT_FOUND = -1L;

    public long searchForwards(final ByteReader reader, final long fromPosition, final long toPosition);

    public long searchBackwards(final ByteReader reader, final long fromPosition, final long toPosition);

}
