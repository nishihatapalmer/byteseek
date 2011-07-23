/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.test;

import java.io.File;
import java.io.FileNotFoundException;
import net.domesdaybook.reader.FileArrayProvider;
import net.domesdaybook.reader.FileByteReader;

/**
 *
 * @author matt
 */
public class Utilities {
    
    
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
    
    
}
