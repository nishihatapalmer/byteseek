/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */


package net.domesdaybook.searcher;

import java.io.IOException;
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
    public long searchForwards(final Reader reader, final long fromPosition) 
            throws IOException {
        return searchForwards(reader, fromPosition, Long.MAX_VALUE);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchForwards(final Reader reader) 
            throws IOException {
        return searchForwards(reader, 0, Long.MAX_VALUE);
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
    public long searchBackwards(final Reader reader, final long fromPosition) 
            throws IOException {
        return searchBackwards(reader, fromPosition, 0);
    }

    
    /**
     * @inheritDoc
     */
    @Override
    public long searchBackwards(final Reader reader) 
            throws IOException {
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
