/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
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
    
    
    
    /**
     * 
     * @param input
     * @param bytes
     * @return
     * @throws IOException
     */
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
    
    
    /**
     * 
     * @param input
     * @param bytes
     * @return
     * @throws IOException
     */
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
    
    
    /**
     * 
     * @param input
     * @param bytes
     * @param fromPosition
     * @return
     * @throws IOException
     */
    public static int readBytes(final RandomAccessFile input, final byte[] bytes, 
            final long fromPosition) throws IOException {
        final int blockSize = bytes.length;
        int totalRead = 0;
        input.seek(fromPosition);
        while (totalRead < blockSize) {
            final int read = input.read(bytes, totalRead, blockSize - totalRead);
            if (read == -1) break;
            totalRead += read;
        }   
        return totalRead;
    }
    
    /**
     * 
     * @param output
     * @param bytes
     * @param atPosition
     * @throws IOException
     */
    public static void writeBytes(final RandomAccessFile output, final byte[] bytes, 
            final long atPosition) throws IOException {
        output.seek(atPosition);
        output.write(bytes);
    }
    
    
    /**
     * 
     * @return
     * @throws IOException
     */
    public static File createTempFile() throws IOException {
        return File.createTempFile("byteseek", ".tmp");
    }
    
    
    /**
     * 
     * @param in
     * @return
     * @throws IOException
     */
    public static File createTempFile(final InputStream in) throws IOException {
        return createTempFile(in, DEFAULT_BUFFER_SIZE);
    }
    
    
    /**
     * 
     * @param in
     * @param bufferSize
     * @return
     * @throws IOException
     */
    public static File createTempFile(final InputStream in, final int bufferSize) throws IOException {
        final File tempFile = createTempFile();
        final FileOutputStream out = new FileOutputStream(tempFile);
        copyStream(in, out, bufferSize);
        out.close();
        return tempFile;
    }
        
    
    /**
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copyStream(final InputStream in,
                                  final OutputStream out) throws IOException {
        copyStream(in, out, DEFAULT_BUFFER_SIZE);
    }
    
    
    /**
     * 
     * @param in
     * @param out
     * @param bufferSize
     * @throws IOException
     */
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
