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
public class FileReaderTest {
    
    public FileReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of readByte method, of class FileReader.
     */
    @Test
    public void testReadByte() throws FileNotFoundException {
        FileReader reader = new FileReader(getFile("/TestASCII.txt"));
        
        test(reader, 112122, (byte) 0x50);
        test(reader, 112271, (byte) 0x44);
        test(reader, 112275, (byte) 0x6d);
        test(reader, 112277, (byte) 0x2e);
        
        reader = new FileReader(getFile("/TestASCII.zip"));
        //TODO: add tests for zip file reading.
        
    }

    private void test(FileReader reader, long position, byte value) {
        assertEquals(value, reader.readByte(position));
    }
    
    
    /**
     * Test of length method, of class FileReader.
     */
    @Test
    public void testLength() throws FileNotFoundException {
         FileReader reader = new FileReader(getFile("/TestASCII.txt"));
         assertEquals("length ASCII", 112280, reader.length());
         
         reader = new FileReader(getFile("/TestASCII.zip"));
         assertEquals("length ZIP", 45846, reader.length());
    }
   
    
    private File getFile(final String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        return new File(url.getPath());
    }      
    
    
}
