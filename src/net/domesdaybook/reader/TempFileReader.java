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

import net.domesdaybook.reader.cache.WindowCache;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Matt Palmer
 */
public final class TempFileReader extends FileReader {

    
    TempFileReader(final InputStream stream) throws IOException {
        this(ReadUtils.createTempFile(stream));
    }
    
    
    TempFileReader(final InputStream stream, final int windowSize) throws IOException {
        this(ReadUtils.createTempFile(stream), windowSize);
    }
    
    
    TempFileReader(final InputStream stream, final int windowSize, 
                  final int capacity) throws IOException {
        this(ReadUtils.createTempFile(stream), windowSize, capacity);
    }    
    
    
    TempFileReader(final InputStream stream, final WindowCache cache) throws IOException {
        this(ReadUtils.createTempFile(stream), cache);
    }    
    
    
    TempFileReader(final InputStream stream, final int windowSize, 
                   final WindowCache cache) throws IOException {
        this(ReadUtils.createTempFile(stream), windowSize, cache);
    }

    
    TempFileReader(final File tempFile) throws FileNotFoundException {
        super(tempFile);
    }

    
    TempFileReader(final File tempFile, final int windowSize) throws FileNotFoundException {
        super(tempFile, windowSize);
    }
    
    
    TempFileReader(final File tempFile, final int windowSize,
                   final int capacity) throws FileNotFoundException {
        super(tempFile, windowSize, capacity);
    }
    
    
    TempFileReader(final File tempFile, final WindowCache cache) throws FileNotFoundException {
        super(tempFile, cache);
    }
    
    
    TempFileReader(final File tempFile, final int windowSize, final WindowCache cache) throws FileNotFoundException {
        super(tempFile, windowSize, cache);
    }
    
    
    @Override
    public void close() throws IOException {
        try {
            super.close(); // ensure the inherited random access file is closed first
        } finally {
            getFile().delete(); // before we attempt to delete the file.
        }
    }
    
}
