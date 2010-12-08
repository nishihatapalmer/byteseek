/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.searcher;

import net.domesdaybook.reader.Bytes;

/**
 *
 * @author matt
 */
public interface Searcher {

    public long searchForwards(final Bytes reader, final long fromPosition, final long toPosition);

    public long searchBackwards(final Bytes reader, final long fromPosition, final long toPosition);

}
