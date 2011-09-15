/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */


package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author matt
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
    public void close() {
        try {
            super.close();
        } finally {
            getFile().delete();
        }
    }
    
}
