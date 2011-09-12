/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.searcher;

import net.domesdaybook.reader.Reader;

/**
 *
 * @author matt
 */
public abstract class AbstractSearcher implements Searcher {

    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader, final long fromPosition) {
        return searchForwards(reader, fromPosition, reader.length() - 1);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader) {
        return searchForwards(reader, 0, reader.length() - 1);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(final byte[] bytes, final int fromPosition) {
        return searchForwards(bytes, fromPosition, bytes.length - 1);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchForwards(final byte[] bytes) {
        return searchForwards(bytes, 0, bytes.length - 1);
    }


    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader, final long fromPosition) {
        return searchBackwards(reader, fromPosition, 0);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader) {
        return searchBackwards(reader, reader.length() - 1, 0);
    }


    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(final byte[] bytes, final int fromPosition) {
        return searchBackwards(bytes, fromPosition, 0);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public int searchBackwards(final byte[] bytes) {
        return searchBackwards(bytes, bytes.length - 1, 0);
    }
    
}
