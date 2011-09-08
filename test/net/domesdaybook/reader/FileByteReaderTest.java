/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.domesdaybook.reader;

import java.io.File;
import java.net.URL;
import java.io.FileNotFoundException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class FileByteReaderTest {
    
    public FileByteReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of readByte method, of class FileByteReader.
     */
    @Test
    public void testReadByte() throws FileNotFoundException {
        FileByteReader reader = new FileByteReader(getFile("/TestASCII.txt"));
        
        test(reader, 112122, (byte) 0x50);
        test(reader, 112271, (byte) 0x44);
        test(reader, 112275, (byte) 0x6d);
        test(reader, 112277, (byte) 0x2e);
    }

    private void test(FileByteReader reader, long position, byte value) {
        assertEquals(value, reader.readByte(position));
    }
    
    /**
     * Test of length method, of class FileByteReader.
     */
    @Test
    public void testLength() {
        System.out.println("length");
        FileByteReader instance = null;
        long expResult = 0L;
        long result = instance.length();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
   
    
    
    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }      
    
    
}
