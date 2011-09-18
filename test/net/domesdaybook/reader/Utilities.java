/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author matt
 */
public class Utilities {
    

    
    public static byte[] getByteArray(String path) throws IOException {
        return getByteArray(new File(path));
    }
                
    
    public static byte[] getByteArray(File file) throws IOException {
        try {
            final Reader reader = new FileReader(file);
            final Window array = reader.getWindow(0);
            return array.getArray();
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
    
    
}
