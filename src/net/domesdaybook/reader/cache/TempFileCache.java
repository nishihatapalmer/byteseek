/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */
 
package net.domesdaybook.reader.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.reader.ReadUtils;
import net.domesdaybook.reader.Window;


/**
 *
 * @author matt
 */
public final class TempFileCache extends AbstractObservableCache {

    private final int windowSize;
    private final File tempFile;
    private final RandomAccessFile file;
    private final Set<Long> windowPositions;
   
    
    public TempFileCache(final int windowSize) throws IOException {
        this.windowSize = windowSize;
        tempFile = ReadUtils.createTempFile();
        file = new RandomAccessFile(tempFile, "rw");
        windowPositions = new HashSet<Long>();
    }
    
    
    @Override
    public Window getWindow(final long position) {
        Window window = null;
        if (windowPositions.contains(position)) {
            byte[] array = new byte[windowSize];
            try {
                final int limit = ReadUtils.readBytes(file, array, position);
                window = new Window(array, position, limit);
            } catch (IOException justReturnNullWindow) {
            }
        }
        return window;
    }

    
    @Override
    public void addWindow(final Window window) {
        final long windowPosition = window.getWindowPosition();
        if (!windowPositions.contains(windowPosition)) {
            try {
                file.seek(windowPosition);
                file.write(window.getArray());
                windowPositions.add(windowPosition);
                notifyWindowAdded(window, this);
            } catch (IOException justFailToAddTheWindow) {
            }
        }
    }

    
    @Override
    public void clear() {
        windowPositions.clear();
    }
    
    
    public File getTempFile() {
        return tempFile;
    }

    
     
}
