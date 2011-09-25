/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 *
 * @author matt
 */
public final class ReadUtils {
    
    private static int DEFAULT_BUFFER_SIZE = 4096;
    
    private ReadUtils() {
    }
    
    
    
    public static int readBytes(final InputStream input, final byte[] bytes) throws IOException {
        final int blockSize = bytes.length;
        int totalRead = 0;
        while (totalRead < blockSize) {
            final int read = input.read(bytes, totalRead, blockSize - totalRead);
            if (read == -1) break;
            totalRead += read;
        }   
        return totalRead;
    }
    
    
    public static int readBytes(final RandomAccessFile input, final byte[] bytes) throws IOException {
        final int blockSize = bytes.length;
        int totalRead = 0;
        while (totalRead < blockSize) {
            final int read = input.read(bytes, totalRead, blockSize - totalRead);
            if (read == -1) break;
            totalRead += read;
        }   
        return totalRead;
    }
    
    
    public static File createTempFile(final InputStream in) throws IOException {
        return createTempFile(in, DEFAULT_BUFFER_SIZE);
    }
    
    
    public static File createTempFile(final InputStream in, final int bufferSize) throws IOException {
        final File tempFile = File.createTempFile("byteseek", ".tmp");
        final FileOutputStream out = new FileOutputStream(tempFile);
        copyStream(in, out, bufferSize);
        out.close();
        return tempFile;
    }
        
    
    public static void copyStream(final InputStream in,
                                  final OutputStream out) throws IOException {
        copyStream(in, out, DEFAULT_BUFFER_SIZE);
    }
    
    
    public static void copyStream(final InputStream in, 
                                  final OutputStream out,
                                  final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        int byteRead = 0;
        while ((byteRead = in.read(buffer)) >= 0) {
            out.write(buffer, 0, byteRead);
        }
    }
    
    
}
