/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 *
 * @author matt
 */
public final class ReadUtils {
    
    
    private ReadUtils() {
    }
    
    
    public static int readBytesFrom(final InputStream input, final byte[] bytes) throws IOException {
        final int blockSize = bytes.length;
        int totalRead = 0;
        while (totalRead < blockSize) {
            int read = input.read(bytes, totalRead, blockSize - totalRead);
            if (read == -1) break;
            totalRead += read;
        }   
        return totalRead;
    }
    
    
    public static int readBytesFrom(final RandomAccessFile input, final byte[] bytes) throws IOException {
        final int blockSize = bytes.length;
        int totalRead = 0;
        while (totalRead < blockSize) {
            int read = input.read(bytes, totalRead, blockSize - totalRead);
            if (read == -1) break;
            totalRead += read;
        }   
        return totalRead;
    }
    
    
}
