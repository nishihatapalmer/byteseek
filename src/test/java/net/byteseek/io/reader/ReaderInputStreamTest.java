package net.byteseek.io.reader;

import net.byteseek.io.IOIterator;
import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.Assert.*;

public class ReaderInputStreamTest {

    private static Random random = new Random();
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
            int read = IOUtils.readBytes(raf, count, buf2);
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
    public void testNullByteArrayRead() throws IOException{
        for (int i = 0; i < fileReaders.length; i++) {
            testNullByteArrayRead(fileReaders[i]);
        }
    }

    private void testNullByteArrayRead(InputStreamReader fileReader) throws IOException {
        InputStream is = new ReaderInputStream(fileReader);
        try {
            is.read(null);
            fail("Expected a NullPointerException.");
        } catch (NullPointerException expected) {}
        try {
            is.read(null, 0, 1024);
            fail("Expected a NullPointerException.");
        } catch (NullPointerException expected) {}
    }

    @Test
    public void testReadIndexOutOfBounds() throws IOException{
        for (int i = 0; i < fileReaders.length; i++) {
            testReadIndexOutOfBounds(fileReaders[i], new byte[1024]);
        }
    }

    @Test
    public void testReadLengthZero() throws IOException{
        for (int i = 0; i < fileReaders.length; i++) {
            testReadLengthZero(fileReaders[i], new byte[1024]);
        }
    }

    private void testReadLengthZero(InputStreamReader fileReader, byte[] bytes) throws IOException {
        ReaderInputStream is = new ReaderInputStream(fileReader);
        assertEquals("Read zero bytes", 0, is.read(bytes, 0, 0));
        assertEquals("Read zero bytes with valid offset", 0, is.read(bytes, 1023, 0));
        assertEquals("Position has not changed", 0, is.getNextReadPos());
    }

    private void testReadIndexOutOfBounds(InputStreamReader fileReader, byte [] testBuffer) throws IOException {
        InputStream is = new ReaderInputStream(fileReader);

        try {
            is.read(testBuffer, testBuffer.length, 1);
            fail("Expected a IndexOutOfBoundsException reading at an offset past the buffer length.");
        } catch (IndexOutOfBoundsException expected) {}

        try {
            is.read(testBuffer, -1, 20);
            fail("Expected a IndexOutOfBoundsException reading at a negative offset.");
        } catch (IndexOutOfBoundsException expected) {}

        try {
            is.read(testBuffer, 0, testBuffer.length + 1);
            fail("Expected a IndexOutOfBoundsException reading a length greater than buffer length.");
        } catch (IndexOutOfBoundsException expected) {}

        try {
            is.read(testBuffer, 1, testBuffer.length);
            fail("Expected a IndexOutOfBoundsException reading a length greater than buffer length minus offset.");
        } catch (IndexOutOfBoundsException expected) {}

    }


    @Test
    public void testReadByteSkip() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            testReadByteSkip(fileReaders[i]);
        }
    }

    private void testReadByteSkip(InputStreamReader fileReader) throws IOException {
        InputStream is = new ReaderInputStream(fileReader);
        long trialPos = random.nextInt(fileLength);
        long finalPos = trialPos + 256 < fileLength? trialPos + 256 : fileLength;
        is.skip(trialPos);
        raf.seek(trialPos);
        for (long pos = trialPos; pos < finalPos; pos++) {
            assertEquals("Bytes at pos " + pos, raf.read(), is.read());
        }
    }

    @Test
    public void testSkipPastEnd() throws IOException {
        for (int i = 0; i < fileReaders.length; i++) {
            testSkipPastEnd(fileReaders[i]);
        }
    }

    private void testSkipPastEnd(InputStreamReader fileReader) throws IOException {
        ReaderInputStream is = new ReaderInputStream(fileReader);
        assertEquals("Not skipped with negative skip", 0, is.skip(-1));
        assertEquals("Not skipped with zero skip", 0, is.skip(0));
        assertEquals("Position is still at zero", 0, is.getNextReadPos());

        assertEquals("Skipped correct number of bytes", (fileLength - 2), fileLength - 2, is.skip(fileLength - 2));
        assertEquals("At correct position " + (fileLength - 2), fileLength - 2, is.getNextReadPos());

        assertEquals("Not skipped past end.", 1, is.skip(1));
        assertEquals("Now skipped past end.", 0, is.skip(1));
        assertTrue("Read is negative once past end.", is.read() < 0);
        assertEquals("Available is zero once past end.", 0, is.available());
        assertEquals("And does not skip after that.", 0, is.skip(9999));
        assertTrue("Read is negative once past end.", is.read() < 0);
        assertEquals("Available is zero once past end.", 0, is.available());
    }

    @Test
    public void testAvailable() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            testAvailable(fileReaders[i]);
        }
    }

    private void testAvailable(InputStreamReader fileReader) throws IOException {
        ReaderInputStream is = new ReaderInputStream(fileReader);
        assertTrue("Bytes are available for new reader.", is.available() > 0);
    }

    @Test
    public void testMarkSupported() throws Exception {
        ReaderInputStream is = new ReaderInputStream(fileReaders[0]);
        assertTrue("Mark is supported by default", is.markSupported());

        is = new ReaderInputStream(fileReaders[1], true);
        assertTrue("Mark is supported by default with close reader", is.markSupported());

        is = new ReaderInputStream(fileReaders[2], true, false);
        assertFalse("Mark is not supported if set false", is.markSupported());

        is = new ReaderInputStream(fileReaders[3], false, false);
        assertFalse("Mark is not supported if set false", is.markSupported());

        is = new ReaderInputStream(fileReaders[4], true, true);
        assertTrue("Mark is not supported if set true", is.markSupported());

        is = new ReaderInputStream(fileReaders[5], false, true);
        assertTrue("Mark is not supported if set ", is.markSupported());
    }

    @Test
    public void testResetIOException() throws Exception {
        ReaderInputStream is = new ReaderInputStream(fileReaders[3], false, false);
        is.mark(1024);
        is.skip(1024);
        try {
            is.reset();
            fail("Expected IO Exception if mark not supported.");
        } catch(IOException expected) {}

        is = new ReaderInputStream(fileReaders[4], true, true);
        is.mark(1024);
        is.skip(1024);
        try {
            is.reset(); // no exception if mark is supported.
        } catch (IOException fail) {
            fail("No IO Exception expected if mark is supported.");
        }
    }

    @Test
    public void testMark() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            testMark(fileReaders[i]);
        }
    }

    private void testMark(InputStreamReader fileReader) throws IOException {
        ReaderInputStream is = new ReaderInputStream(fileReader);
        is.skip(24);
        is.mark(2048);
        is.skip(1025-24);
        assertEquals("Position is 1025", 1025, is.getNextReadPos());
        is.reset();
        assertEquals("Position is back to 24", 24, is.getNextReadPos());
        is.skip(10000);
        long newposition = is.getNextReadPos();
        is.mark(0);
        is.skip(10000);
        is.reset();
        assertEquals("Position resets even with 0 readahead", newposition, is.getNextReadPos());

    }



        @Test
    public void testClose() throws Exception {
        CloseReader reader = new CloseReader();
        assertFalse("Reader is not closed", reader.closed);
        ReaderInputStream is = new ReaderInputStream(reader);
        is.close();
        assertTrue("InputStream now closed", is.getNextReadPos() < 0);
        assertTrue("Reader is now closed.", reader.closed);

        reader = new CloseReader();
        is = new ReaderInputStream(reader, false);
        assertFalse("Reader is not closed", reader.closed);
        is.close();
        assertTrue("InputStream now closed", is.getNextReadPos() < 0);
        assertFalse("Reader is not closed", reader.closed);

        reader = new CloseReader();
        is = new ReaderInputStream(reader, true);
        assertFalse("Reader is not closed", reader.closed);
        is.close();
        assertTrue("InputStream now closed", is.getNextReadPos() < 0);
        assertTrue("Reader is now closed.", reader.closed);
    }


    private static class CloseReader implements WindowReader {

        public boolean closed;

        @Override
        public IOIterator<Window> iterator() {
            return null;
        }

        @Override
        public void setWindowFactory(WindowFactory factory) {
        }

        @Override
        public void close() throws IOException {
            closed = true;
        }

        @Override
        public int readByte(long position) throws IOException {
            return 0;
        }

        @Override
        public int read(long position, byte[] readInto) throws IOException {
            return 0;
        }

        @Override
        public int read(long position, byte[] readInto, int readIntoPos, int readLength) throws IOException {
            return 0;
        }

        @Override
        public int read(long position, ByteBuffer buffer) throws IOException {
            return 0;
        }

        @Override
        public Window getWindow(long position) throws IOException {
            return null;
        }

        @Override
        public int getWindowOffset(long position) {
            return 0;
        }

        @Override
        public long length() throws IOException {
            return 0;
        }
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