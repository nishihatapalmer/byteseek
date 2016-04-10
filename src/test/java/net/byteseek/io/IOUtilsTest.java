package net.byteseek.io;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

public class IOUtilsTest {

    File asciiFile;
    File zipFile;

    @Before
    public void setUp() {
        asciiFile = getFile("/TestASCII.txt");
        zipFile   = getFile("/TestASCII.zip");
    }

    @Test
    public void testReadEntireFile() throws Exception {
        testReadEntireFile(asciiFile);
        testReadEntireFile(zipFile);
    }

    private void testReadEntireFile(File file) throws Exception {
        byte[] asciiFileArray = IOUtils.readEntireFile(file);
        assertEquals("Read correct size", file.length(), asciiFileArray.length);
        assertFileBytesEqual(file, asciiFileArray);
    }


    @Test
    public void testReadBytes() throws Exception {

    }

    @Test
    public void testReadBytes1() throws Exception {

    }

    @Test
    public void testReadBytes2() throws Exception {

    }

    @Test
    public void testWriteBytes() throws Exception {

    }

    @Test
    public void testCreateTempFile() throws Exception {

    }

    @Test
    public void testCreateTempFile1() throws Exception {

    }

    @Test
    public void testCreateTempFile2() throws Exception {

    }

    @Test
    public void testCreateTempFile3() throws Exception {

    }

    @Test
    public void testCopyStream() throws Exception {

    }

    @Test
    public void testCopyStream1() throws Exception {

    }

    private void assertFileBytesEqual(File file, byte[] array) throws IOException {
        InputStream asciiStream = new FileInputStream(file);
        long totalRead = 0;
        byte[] buffer = new byte[4096];
        final long length = file.length();
        while (totalRead < length) {
            final int read = asciiStream.read(buffer);
            int filePos = (int) totalRead;
            for (int bufferPos = 0; bufferPos < read; bufferPos++) {
                final byte fileByte = array[filePos++];
                final byte bufferByte = buffer[bufferPos];
                if (fileByte != bufferByte) {
                    fail("Bytes do not match at file position " + (totalRead + bufferPos));
                }
            }
            totalRead += read;
        }
    }


    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }
}