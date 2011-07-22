/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

/**
 *
 * @author matt
 */
public interface ByteArrayProvider {
    
    ByteArray getByteArray(final long position);
    
    long length();
}
