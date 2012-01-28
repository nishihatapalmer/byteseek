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
 */
 
package net.domesdaybook.reader.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import net.domesdaybook.reader.ReadUtils;
import net.domesdaybook.reader.Window;


/**
 *
 * @author Matt Palmer
 */
public final class TempFileCache extends AbstractCache {

    private final int windowSize;
    private final Map<Long, WindowInfo> windowPositions;
    
    private File tempFile;
    private RandomAccessFile file;
    private long nextFilePos;
   
    
    /**
     * 
     * @param windowSize
     * @throws IOException
     */
    public TempFileCache(final int windowSize) {
        this.windowSize = windowSize;
        windowPositions = new HashMap<Long, WindowInfo>();
    }
    
    
    /**
     * 
     * @param position
     * @return
     */
    @Override
    public Window getWindow(final long position) {
        Window window = null;
        final WindowInfo info = windowPositions.get(position);
        if (info != null) {
            final byte[] array = new byte[windowSize];
            try {
                ReadUtils.readBytes(file, array, info.filePosition);
                window = new Window(array, position, info.limit);
            } catch (IOException justReturnNullWindow) {
            }
        }
        return window;
    }

    
    /**
     * 
     * @param window
     */
    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        final WindowInfo info = windowPositions.get(windowPosition);
        if (info == null) {
            try {
                createFileIfNotExists();
                file.seek(nextFilePos);
                file.write(window.getArray());
                windowPositions.put(windowPosition, 
                                    new WindowInfo(window.length(), nextFilePos));
                nextFilePos += windowSize;
                notifyWindowFree(window, this);
            } catch (IOException justFailToAddTheWindow) {
            }
        }
    }

    
    /**
     * 
     */
    @Override
    public void clear() {
        windowPositions.clear();
        deleteFileIfExists();
    }
    
    
    /**
     * 
     * @return
     */
    public File getTempFile() {
        return tempFile;
    }
    
    
    private void createFileIfNotExists() throws IOException {
        if (tempFile == null) {
            windowPositions.clear();
            nextFilePos = 0;
            tempFile = ReadUtils.createTempFile();
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
    
    
    private static class WindowInfo {
        
        final int limit;
        final long filePosition;  
        
        public WindowInfo(final int limit, final long filePosition) {
            this.limit = limit;
            this.filePosition = filePosition;
        }

    } 
}
