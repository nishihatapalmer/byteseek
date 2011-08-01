/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.reader;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author matt
 */
public class Utilities {
    
    
    public static FileByteReader getFileByteReader(final String path) {
        return getFileByteReader(new File(path));
    }
    
    
    public static FileByteReader getFileByteReader(final File file) {
        try {
            return new FileByteReader(file);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
    
    
    public static FileArrayProvider getFileArrayProvider(final File file) {
        try {
            return new FileArrayProvider(file);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

    
    public static byte[] getByteArray(String path) {
        return getByteArray(new File(path));
    }
                
    
    public static byte[] getByteArray(File file) {
        try {
            final ArrayProvider provider = new FileArrayProvider(file);
            final Array array = provider.getByteArray(0);
            return array.getArray();
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
    
    
}
