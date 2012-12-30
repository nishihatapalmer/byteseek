/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.io;

import java.io.File;
import java.io.IOException;

import net.domesdaybook.io.FileWindowReader;
import net.domesdaybook.io.WindowReader;
import net.domesdaybook.io.Window;

/**
 *
 * @author matt
 */
public class Utilities {
    

    
    /**
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] getByteArray(final String path) throws IOException {
        return getByteArray(new File(path));
    }
                
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getByteArray(final File file) throws IOException {
        final WindowReader reader = new FileWindowReader(file);
        final Window window = reader.getWindow(0);
        return window.getArray();
    }
    
    
}
