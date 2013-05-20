/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.byteseek.io;

import java.io.File;
import java.io.IOException;

import net.byteseek.io.FileReader;
import net.byteseek.io.Window;
import net.byteseek.io.WindowReader;

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
        final WindowReader reader = new FileReader(file);
        final Window window = reader.getWindow(0);
        reader.close();
        return window.getArray();
    }
    
    
}
