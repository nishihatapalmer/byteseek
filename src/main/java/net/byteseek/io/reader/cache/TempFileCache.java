/*
 * Copyright Matt Palmer 2011-2018, All rights reserved.
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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.windows.*;
import net.byteseek.utils.collections.PositionHashMap;


/**
 * A {@link WindowCache} which stores {@link net.byteseek.io.reader.windows.Window} objects
 * into a temporary file for later retrieval.  It maintains a map of the start positions
 * of each window against the position in the file where the Window was stored.
 * <p>
 * A temporary file is only created if a Window is added to the cache, and it is
 * deleted when the cache is cleared.
 * 
 * @author Matt Palmer
 */
public final class TempFileCache extends AbstractFreeNotificationCache implements SoftWindowRecovery {

    private final static int DEFAULT_CAPACITY = 1024; // number of positions to cache initially = 4Mb file if 4096 byte windows are cached.
    private final PositionHashMap<WindowInfo> windowPositions;
    private final File tempDir;
    private File tempFile;
    private RandomAccessFile file;
    private long nextFilePos;

    /**
     * Constructs a TempFileCache using the default temporary directory and an initial cache capacity
     * of 1024 Window records in memory.  The cache capaccapacityity is the number of Window records
     * the cache will hold in memory, not the size of the temporary file.  Both can expand as required.
     */
    public TempFileCache() {
        this(null, DEFAULT_CAPACITY);
    }

    /**
     * Constructs a TempFileCache which creates temporary files in the directory specified,
     * with an initial cache capacity of 1024.  The cache capacity is the number of Window records
     * the cache will hold in memory, not the size of the temporary file.  Both can expand as required.
     * If the file is null, then temporary files will be created in the default temp directory.
     *
     * @param tempDir The directory to create temporary files in.
     * @throws java.lang.IllegalArgumentException if the tempdir supplied is not null and not a directory.
     */
    public TempFileCache(final File tempDir) {
        this(tempDir, DEFAULT_CAPACITY);
    }

    /**
     * Constructs a TempFileCache which creates temporary files in the directory specified,
     * with a specified initial cache capacity.  The cache capacity is the number of Window records
     * the cache will hold in memory, not the size of the temporary file.  Both can expand as required.
     * If the file is null, then temporary files will be created in the default temp directory.
     * If the defaultCapacity is zero or negative, the standard default capacity of 1024 will be used.
     *
     * @param tempDir The directory to create temporary files in.
     * @param defaultCapacity The number of Windows the cache is initalised to hold (will resize upwards as required).
     * @throws java.lang.IllegalArgumentException if the tempdir supplied is not null and not a directory.
     */
    public TempFileCache(final File tempDir, final int defaultCapacity) {
        windowPositions = new PositionHashMap<WindowInfo>(defaultCapacity > 0? defaultCapacity : DEFAULT_CAPACITY);
        this.tempDir = tempDir;
        if (tempDir != null && !tempDir.isDirectory()) {
            throw new IllegalArgumentException("The temp dir file supplied is not a directory: " + tempDir.getAbsolutePath());
        }
    }

    @Override
    public Window getWindow(final long position) throws IOException {
        Window window = null;
        final WindowInfo info = windowPositions.get(position);
        if (info != null) {
            final byte[] array = new byte[info.length];
            IOUtils.readBytes(file, info.filePosition, array);
            window = new SoftWindow(array, position, info.length, this);
        }
        return window;
    }

    @Override
    public void addWindow(final Window window) throws IOException {
        final long windowPosition = window.getWindowPosition();
        final WindowInfo info = windowPositions.get(windowPosition);
        if (info == null) {
            createFileIfNotExists();
            file.seek(nextFilePos);
            file.write(window.getArray(), 0, window.length());
            windowPositions.put(windowPosition, new WindowInfo(window.length(), nextFilePos));
            nextFilePos += window.length();
        }
    }

    @Override
    public int read(final long windowPos, final int offset, final byte[] readInto, final int readIntoPos) throws IOException {
        int bytesRead = 0;
        if (file != null) {

            // Get each contiguous cached window and write it into the array,
            // until there are no more cached windows, or we have written enough bytes.
            final int bytesRequired = readInto.length - readIntoPos;
            final PositionHashMap<WindowInfo> localPositions = windowPositions;
            long readPos = windowPos;
            int readOffset = offset;
            WindowInfo info;
            while ((info = localPositions.get(readPos)) != null && bytesRead < bytesRequired) {

                // Have a window at the curent readPos - fill as many bytes as we can from it:
                final int remainingBytes = bytesRequired - bytesRead;
                final int windowBytes    = info.length - readOffset;
                final int bytesToCopy    = remainingBytes < windowBytes? remainingBytes : windowBytes;
                final long filePos       = info.filePosition + readOffset;
                int bytesCopied = IOUtils.readBytes(file, filePos, readInto, readIntoPos, bytesToCopy);
                if (bytesCopied == 0) {
                    break; // shouldn't happen if the cached positions are within the file, but better to be safe.
                }
                bytesRead += bytesCopied;
                readPos += info.length;
                readOffset= 0;
            }
        }
        return bytesRead;
    }

    /**
     * Clears the map of Window positions to their position and size in the file,
     * and deletes the temporary file if it exists.
     */
    @Override
    public void clear() throws IOException {
        windowPositions.clear();
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
            windowPositions.clear();
            nextFilePos = 0;
            tempFile = tempDir == null? IOUtils.createTempFile()
                                      : IOUtils.createTempFile(tempDir);
            file = new RandomAccessFile(tempFile, "rw");
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
                nextFilePos = 0;
            }
            if (fileCloseException != null || !tempFileDeleted) {
                throw tempFileDeleted? fileCloseException
                                     : new TempFileNotDeletedException(fileDetails, fileCloseException);
            }
        }
    }

    @Override
    public byte[] reloadWindowBytes(final Window window) throws IOException {
        if (file == null) {
            throw new WindowMissingException("Can't reload bytes for window " + window + " : cache file does not exist.");
        }
        final WindowInfo info = windowPositions.get(window.getWindowPosition());
        if (info != null) {
            final byte[] array = new byte[info.length];
            IOUtils.readBytes(file, info.filePosition, array);
            return array;
        }
        throw new WindowMissingException("Can't reload bytes for window " + window + " : not found in the cache.");
    }

    /**
     * A utility class recording the length of a Window and the position in 
     * the temporary file it exists at.
     */
    private static final class WindowInfo {
        
        final int length;
        final long filePosition;  
        
        public WindowInfo(final int limit, final long filePosition) {
            this.length = limit;
            this.filePosition = filePosition;
        }
    } 
    
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(temp file: " + tempFile + " window positions recorded:" + windowPositions.size() + ')';
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
