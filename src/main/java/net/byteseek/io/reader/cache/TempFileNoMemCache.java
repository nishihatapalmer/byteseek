/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
 */
 
package net.byteseek.io.reader.cache;

import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.windows.SoftWindow;
import net.byteseek.io.reader.windows.SoftWindowRecovery;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowMissingException;
import net.byteseek.utils.ArgUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A {@link WindowCache} which stores {@link Window} objects
 * into a temporary file for later retrieval.  The Window objects are stored in exactly the
 * same absolute position in the temporary file that they represent in the window.
 * The cache maintains no memory of what has been placed into it.
 * It will simply return as many bytes as it can from the file at the window position supplied,
 * up to a fixed window size which is required to use this cache.
 * <p>
 * An attempt to read from the cache before anything has been stored will result in a WindowMissingException,
 * and an attempt to read from after the end of what has been written to the cache will result in an EOFException.
 * <p>
 * A temporary file is only created if a Window is added to the cache, and it is
 * deleted when the cache is cleared.
 *
 * @author Matt Palmer
 */
public final class TempFileNoMemCache extends AbstractFreeNotificationCache implements SoftWindowRecovery {

    private final int windowSize;
    private final File tempDir;
    private File tempFile;
    private RandomAccessFile file;

    /**
     * Constructs a TempFileCache given a windowSize
     * @param windowSize The size of the windows the cache will use.
     */
    public TempFileNoMemCache(final int windowSize) {
        this(null, windowSize);
    }

    /**
     * Constructs a TempFileCache which creates temporary files in the directory specified.
     * If the file is null, then temporary files will be created in the default temp directory.
     *
     * @param tempDir The directory to create temporary files in.
     * @param windowSize The size of the windows the cache will use.
     * @throws IllegalArgumentException if the tempdir supplied is not a directory, or the windowSize is not a postive integer.
     */
    public TempFileNoMemCache(final File tempDir, final int windowSize) {
        this.tempDir = tempDir;
        this.windowSize = windowSize;
        ArgUtils.checkPositiveInteger(windowSize, "windowSize");
        if (tempDir != null && !tempDir.isDirectory()) {
            throw new IllegalArgumentException("The temp dir file supplied is not a directory: " + tempDir.getAbsolutePath());
        }
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        checkFileExists();
        Window window = null;
        final byte[] array = new byte[windowSize];
        final int length = IOUtils.readBytes(file, position, array);
        return new SoftWindow(array, position, length, this);
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        createFileIfNotExists();
        file.seek(window.getWindowPosition());
        file.write(window.getArray(), 0, window.length());
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <b>Warning</b> This cache has no memory of what has been cached.  It will simply read from the temp file
     * at the windowPos + offset location in to the byte array at readIntoPos.  It will try to fill up the remaining
     * array, up to the end of the file.  If a Window was never previously written at those locations, the array contents
     * are undefined.  They may be blank, random, or contain previous file data, depending on the file system.
     */
    @Override
    public int read(final long windowPos, final int offset, final byte[] readInto, final int readIntoPos) throws IOException {
        int bytesRead = 0;
        if (file != null) {
            final int bytesToRead = readInto.length - readIntoPos;
            bytesRead = IOUtils.readBytes(file, windowPos + offset, readInto, readIntoPos, bytesToRead);
        }
        return bytesRead;
    }

    /**
     * Clears the map of Window positions to their position and size in the file,
     * and deletes the temporary file if it exists.
     */
    @Override
    public void clear() throws IOException {
        deleteFileIfExists();
    }

    /**
     * Returns the temporary file backing this cache object.
     * 
     * @return File The temporary file backing this cache object, or null if it doesn't exist.
     */
    public File getTempFile() {
        return tempFile;
    }
    
    
    private void createFileIfNotExists() throws IOException {
        if (tempFile == null) {
            tempFile = tempDir == null? IOUtils.createTempFile()
                                      : IOUtils.createTempFile(tempDir);
            file = new RandomAccessFile(tempFile, "rw");
        }
    }

    private void checkFileExists() throws IOException {
        if (file == null) {
            throw new WindowMissingException("Cache temp file does not exist.");
        }
    }
    

    private void deleteFileIfExists() throws IOException {
        if (tempFile != null) {
            IOException fileCloseException = null;
            String      fileDetails = "";
            boolean tempFileDeleted;
            try {
                file.close();
            } catch (IOException ex) {
                fileCloseException = ex;
            } finally {
                file = null;
                tempFileDeleted = tempFile.delete();
                if (!tempFileDeleted) {
                    fileDetails = tempFile.getAbsolutePath();
                }
                tempFile = null;
            }
            if (fileCloseException != null || !tempFileDeleted) {
                throw tempFileDeleted? fileCloseException
                                     : new TempFileNotDeletedException(fileDetails, fileCloseException);
            }
        }
    }

    @Override
    public byte[] reloadWindowBytes(final Window window) throws IOException {
        checkFileExists();
        final byte[] array = new byte[windowSize];
        IOUtils.readBytes(file, window.getWindowPosition(), array);
        return array;
    }
    
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(temp file: " + tempFile + ')';
	}

    private static class TempFileNotDeletedException extends IOException {

        /**
         * Constructs a TempFileNotDeletedException from a descriptive message and a Throwable cause.
         *
         * @param message The message to include with the exception.
         * @param cause   The Throwable which caused this exception to be thrown.
         */
        public TempFileNotDeletedException(final String message, final Throwable cause) {
            super(message, cause);
        }

    }
}
