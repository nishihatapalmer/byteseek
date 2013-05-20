/*
 * Copyright Matt Palmer 2011-2012, All rights reserved.
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
 
package net.byteseek.io.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import net.byteseek.io.IOUtils;
import net.byteseek.io.Window;


/**
 * A {@link WindowCache} which stores {@link net.byteseek.io.Window} objects
 * into a temporary file for later retrieval.  It maintains a map of the start positions
 * of each window against the position in the file where the Window was stored.
 * <p>
 * A temporary file is only created if a Window is added to the cache, and it is
 * deleted when the cache is cleared.
 * 
 * @author Matt Palmer
 */
public final class TempFileCache extends AbstractCache {

    private final Map<Long, WindowInfo> windowPositions;
    
    private File tempFile;
    private RandomAccessFile file;
    private long nextFilePos;
   
    
    /**
     * Constructs a TempFileCache.
     */
    public TempFileCache() {
        windowPositions = new HashMap<Long, WindowInfo>();
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Window getWindow(final long position) {
        Window window = null;
        final WindowInfo info = windowPositions.get(position);
        if (info != null) {
            final byte[] array = new byte[info.length];
            try {
                IOUtils.readBytes(file, array, info.filePosition);
                window = new Window(array, position, info.length);
            } catch (IOException justReturnNullWindow) {
            }
        }
        return window;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        final WindowInfo info = windowPositions.get(windowPosition);
        if (info == null) {
            try {
                createFileIfNotExists();
                file.seek(nextFilePos);
                file.write(window.getArray(), 0, window.length());
                windowPositions.put(windowPosition, 
                                    new WindowInfo(window.length(), nextFilePos));
                nextFilePos += window.length();
                notifyWindowFree(window, this);
            } catch (IOException justFailToAddTheWindow) {
            }
        }
    }

    
    /**
     * Clears the map of Window positions to their position and size in the file,
     * and deletes the temporary file if it exists.
     */
    @Override
    public void clear() {
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
            tempFile = IOUtils.createTempFile();
            file = new RandomAccessFile(tempFile, "rw");
        }
    }
    

    private void deleteFileIfExists() {
        if (tempFile != null) {
            try {
                file.close();
            } catch (IOException ex) {
            } finally {
                file = null;
                tempFile.delete();
                tempFile = null;
                nextFilePos = 0;
            }
        }
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
}
