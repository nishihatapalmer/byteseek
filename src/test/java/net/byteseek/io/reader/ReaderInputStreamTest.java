package net.byteseek.io.reader;

import net.byteseek.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class ReaderInputStreamTest {

    private InputStreamReader[] fileReaders = new InputStreamReader[10];
    private int[]               windowSizes = new int[] {512, 1022, 1023, 1024, 1025, 1026, 4096, 32, 127, 157};
    private RandomAccessFile    raf;
    private int                 fileLength;

    @Before
    public void setup() throws IOException {
        // Set up readers with different window sizes
        for (int i = 0; i < 10; i++) {
            FileInputStream filein = getFileInputStream("/TestBigRandom.rnd");
            fileReaders[i] = new InputStreamReader(filein, windowSizes[i]);
        }
        raf = new RandomAccessFile(getFile("/TestBigRandom.rnd"), "r");
        fileLength = (int) raf.length();
    }

    @After
    public void after() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            fileReaders[i].close();
        }
        raf.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullReader() throws Exception {
        new ReaderInputStream(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullReaderCloseReaderTrue() throws Exception {
        new ReaderInputStream(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullReaderCloseReaderFalse() throws Exception {
        new ReaderInputStream(null, false);
    }


    @Test
    public void testRead() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            testRead(fileReaders[i]);
        }
    }

    private void testRead(InputStreamReader fileReader) throws IOException {
        InputStream is = new ReaderInputStream(fileReader);
        byte[] buf = new byte[193];
        byte[] buf2 = new byte[193];
        long count = 0;
        long readBytes;
        while ((readBytes = is.read(buf, 0, 193)) > 0) {
            int read = IOUtils.readBytes(raf, buf2, count);
            for (int i = 0; i < readBytes; i++) {
                if (buf[i] != buf2[i]) {
                    fail("Mismatch in bytes detected at position " + count + i + " stream byte value  " + buf[i] + " raf byte value " + buf2[i]);
                }
            }
            count += readBytes;
        }
        assertEquals("Bytes read from stream is file length", fileLength, count);
    }

    @Test
    public void testRead1() throws Exception {

    }

    @Test
    public void testAvailable() throws Exception {

    }

    @Test
    public void testMarkSupported() throws Exception {

    }

    @Test
    public void testMark() throws Exception {

    }

    @Test
    public void testReset() throws Exception {

    }

    @Test
    public void testClose() throws Exception {

    }

    private FileInputStream getFileInputStream(final String resourceName) throws IOException {
        return new FileInputStream(getFile(resourceName));
    }

    private File getFile(final String resourceName) throws IOException {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }

}