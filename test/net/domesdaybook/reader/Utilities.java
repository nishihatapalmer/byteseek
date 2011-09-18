/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author matt
 */
public class Utilities {
    

    
    public static byte[] getByteArray(final String path) throws IOException {
        return getByteArray(new File(path));
    }
                
    
    public static byte[] getByteArray(final File file) throws IOException {
        final Reader reader = new FileReader(file);
        final Window window = reader.getWindow(0);
        return window.getArray();
    }
    
    
}
