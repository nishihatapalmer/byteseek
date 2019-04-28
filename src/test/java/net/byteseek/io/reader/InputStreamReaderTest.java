/*
 * Copyright Matt Palmer 2015, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.byteseek.io.reader;

import net.byteseek.io.IOIterator;
import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.cache.NoCache;
import net.byteseek.io.reader.cache.TestWindow;
import net.byteseek.io.reader.windows.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.junit.Assert.*;

public class InputStreamReaderTest {

    Random rand = new Random();

    private InputStreamReader[] readers     = new InputStreamReader[10];
    private InputStreamReader[] fileReaders = new InputStreamReader[10];
    private int[]               windowSizes = new int[] {512, 1022, 1023, 1024, 1025, 1026, 4096, 32, 127, 157};
    private RandomAccessFile    raf;
    private int                fileLength;


    @Before
    public void setup() throws Exception {
        byte[] array = new byte[1024];
        // Set up readers with different window sizes
        for (int i = 0; i < 10; i++) {
            InputStream in = new ByteArrayInputStream(array);
            readers[i] = new InputStreamReader(in, windowSizes[i]);
            FileInputStream filein = getFileInputStream("/TestASCII.txt");
            fileReaders[i] = new InputStreamReader(filein, windowSizes[i]);
        }
        raf = new RandomAccessFile(getFile("/TestASCII.txt"), "r");
        fileLength = (int) raf.length();
    }

    @After
    public void after() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            fileReaders[i].close();
        }
        raf.close();
    }


    @Test
    public void testRead() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            testRead(fileReaders[i]);
        }
    }

    private void testRead(InputStreamReader fileReader) throws IOException {
        byte[] buf = new byte[193];
        byte[] buf2 = new byte[193];
        long count = 0;
        long readBytes;
        while ((readBytes = fileReader.read(count, buf)) > 0) {
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
    public void testReadByteBuffer() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            testReadBuffer(fileReaders[i]);
        }
    }

    private void testReadBuffer(InputStreamReader fileReader) throws IOException {
        byte[] buf = new byte[193];
        byte[] buf2 = new byte[193];
        ByteBuffer buffer = ByteBuffer.wrap(buf);
        long count = 0;
        long readBytes;
        while ((readBytes = fileReader.read(count, buffer)) > 0) {
            int read = IOUtils.readBytes(raf, count, buf2);
            for (int i = 0; i < readBytes; i++) {
                if (buf[i] != buf2[i]) {
                    fail("Mismatch in bytes detected at position " + count + i + " stream byte value  " + buf[i] + " raf byte value " + buf2[i]);
                }
            }
            count += readBytes;
            buffer.clear();
        }
        assertEquals("Bytes read from stream is file length", fileLength, count);
    }


    @Test
    public void testIterateWindows() throws IOException {
        for (int i = 0; i < readers.length;i++) {
            testIterateReader(readers[i]);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoRemoveIterator() throws IOException {
        IOIterator<Window> iterator = readers[0].iterator();
        iterator.remove();
    }

    private void testIterateReader(WindowReader reader) throws IOException {
        long length = 0;
        IOIterator<Window> winIterator = reader.iterator();
        while (winIterator.hasNext()) {
            length += winIterator.next().length();
        }
        assertEquals("Length is 1024 after iterating all windows", 1024, length);
    }

    @Test
    public void testReadByte() throws IOException {
        for (int i = 0; i < fileReaders.length; i++) {
            WindowReader reader = fileReaders[i];
            testReadByte(reader, 112122, (byte) 0x50);
            testReadByte(reader, 112271, (byte) 0x44);
            testReadByte(reader, 112275, (byte) 0x6d);
            testReadByte(reader, 112277, (byte) 0x2e);
            testRandomPositions("ascii file:", raf, reader, fileLength);
        }
    }

    @Test
    public void testSetNullFactory() {
        for (int i = 0; i < fileReaders.length; i++) {
            try {
                fileReaders[i].setWindowFactory(null);
                fail("Setting null window factory should give an IllegalArgumentException " + fileReaders[i]);
            } catch (IllegalArgumentException expected) {}
        }
    }

    @Test
    public void testSetWindowFactory() throws IOException {
        for (int i = 0; i < fileReaders.length; i++) {

            Window window = fileReaders[i].getWindow(0);
            assertEquals(HardWindow.class, window.getClass());

            fileReaders[i].setWindowFactory(TestWindow.FACTORY);
            window = fileReaders[i].getWindow(windowSizes[i]);
            assertEquals(TestWindow.class, window.getClass());
        }
    }


    @Test
    public void testReadByteNegative() throws IOException {
        for (int i = 0; i < fileReaders.length; i++) {
            int negPos = -(rand.nextInt());
            assertEquals("negative position gives -1", -1, fileReaders[i].readByte(negPos));
        }
    }

    @Test
    public void testReadBytePastEnd() throws IOException {
        for (int i = 0; i < fileReaders.length; i++) {
            long pos = fileLength + rand.nextInt();
            assertEquals("past end position gives -1", -1, fileReaders[i].readByte(pos));
        }
    }

    @Test
    public void testGetWindowData() throws IOException {
        for (int i = 0; i < fileReaders.length;i++) {
            testGetWindowData(fileReaders[i]);
        }
    }

    private void testGetWindowData(WindowReader fileReader) throws IOException {
        IOIterator<Window> winIterator = fileReader.iterator();
        while (winIterator.hasNext()) {
            final Window window = winIterator.next();
            byte[] fileBytes = new byte[window.length()];
            long windowPosition = window.getWindowPosition();
            raf.seek(windowPosition);
            IOUtils.readBytes(raf, windowPosition, fileBytes);
            assertAllBytesSame(window, fileBytes);
        }
    }

    private void assertAllBytesSame(Window window, byte[] fileBytes) throws IOException {
        byte[] windowArray = window.getArray();
        for (int i = 0; i < fileBytes.length; i++) {
            assertEquals("Bytes identical for window" + window + " at position " + i, fileBytes[i], windowArray[i]);
        }
    }

    @Test
    public void testGetWindowOffset() {
        for (int i = 0; i < readers.length; i++) {
            testWindowOffset(readers[i], windowSizes[i]);
        }
    }

    private void testWindowOffset(WindowReader reader, int windowSize) {
        for (int i = 0; i < 10; i++) {
            // Test integer positions:
            long testPosition = rand.nextInt();
            int offset = (int) (testPosition % (long) windowSize);
            assertEquals("Position " + testPosition, offset, reader.getWindowOffset(testPosition));

            // Test long positions
            testPosition = rand.nextLong();
            offset = (int) (testPosition % (long) windowSize);
            assertEquals("Position " + testPosition, offset, reader.getWindowOffset(testPosition));
        }
    }

    @Test
    public void testGetNegativeWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window before 0: " + i, readers[i].getWindow(-1));
        }
    }

    @Test
    public void testGetZeroWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            Window window = readers[i].getWindow(0);
            assertNotNull("have window at 0: " + i, window);
            assertEquals("window is at zero:" + i, 0, window.getWindowPosition());
        }
    }

    @Test
    public void testGetMidWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNotNull("Have window at 512: " + i, readers[i].getWindow(512));
        }
    }

    @Test
    public void testWindowAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].getWindow(1025));
        }
    }

    @Test
    public void testWindowLongAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].getWindow(200000));
        }
    }


    @Test
    public void testCreateNegativeWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].createWindow(-1));
        }
    }

    @Test
    public void testCreateZeroWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            Window window = readers[i].createWindow(0);
            assertNotNull("have window at 0: " + i, window);
            assertEquals("window is at zero:" + i, 0, window.getWindowPosition());
        }
    }

    @Test
    public void testCreateMidWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNotNull("Have window at 512: " + i, readers[i].createWindow(512));
        }
    }

    @Test
    public void testCreateWindowAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].createWindow(1025));
        }
    }

    @Test
    public void testCreateWindowLongAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].createWindow(200000));
        }
    }

    @Test
    public void testLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertEquals("reader length is 1024", 1024, readers[i].length());
        }
    }

    @Test
    public void testReadEntireStream() throws Exception {
        for (int i = 0; i < fileReaders.length; i++) {
            final long length = fileReaders[i].readEntireStream();
            assertEquals("reader length is " + fileLength, fileLength, length);
        }
    }

    @Test
    public void testCloseAfterReading() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            readers[i].length(); // force read of stream
            readers[i].close();  // close reader.
            try {
                readers[i].getWindow(0);
                fail("Expected IOException");
            } catch (IOException expected) {}
        }
    }

    /* Closing a ByteArrayInputStream doesn't do anything, so this
       test doesn't test anything.
    @Test
    public void testCloseBeforeReading() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            readers[i].close();  // close reader.
            try {
                readers[i].getWindow(0);
                fail("Expected WindowMissingException");
            } catch (WindowMissingException expected) {}
        }
    }
    */

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStream() throws Exception {
        new InputStreamReader(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamWindowSize() throws Exception {
        new InputStreamReader(null, 1024);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamFalse() throws Exception {
        new InputStreamReader(null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamTrue() throws Exception {
        new InputStreamReader(null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamWindowCache() throws Exception {
        new InputStreamReader(null, new NoCache());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullWindowCache() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[1]);
        new InputStreamReader(in, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullWindowCacheFalse() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[1]);
        new InputStreamReader(in, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullWindowCacheTrue() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[1]);
        new InputStreamReader(in, null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullWindowCacheAndStream() throws Exception {
        new InputStreamReader(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullWindowCacheAndStreamFalse() throws Exception {
        new InputStreamReader(null, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullWindowCacheAndStreamTrue() throws Exception {
        new InputStreamReader(null, null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStreamNullWindowCacheFalse() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[1]);
        new InputStreamReader(in, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStreamNullWindowCacheTrue() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[1]);
        new InputStreamReader(in, null, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamWindowCacheFalse() throws Exception {
        new InputStreamReader(null, new NoCache(), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamWindowCacheTrue() throws Exception {
        new InputStreamReader(null, new NoCache(), true);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeCache() throws Exception {
        new InputStreamReader(null, 1024, new NoCache());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeCacheFalse() throws Exception {
        new InputStreamReader(null, 1024, new NoCache(), false);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeCacheTrue() throws Exception {
        new InputStreamReader(null, 1024, new NoCache(), true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStreamSizeNullCache() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[1]);
        new InputStreamReader(in, 1024, null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeCapacity() throws Exception {
        new InputStreamReader(null, 1024, 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeCapacityFalse() throws Exception {
        new InputStreamReader(null, 1024, 1000, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeCapacityTrue() throws Exception {
        new InputStreamReader(null, 1024, 1000, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeFalse() throws Exception {
        new InputStreamReader(null, 1024, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullStreamSizeTrue() throws Exception {
        new InputStreamReader(null, 1024, true);
    }

    @Test
    public void testCreateStream() {
        try {
            new InputStreamReader(new ByteArrayInputStream(new byte[1]));
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), false);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), true);
        } catch (Exception e) {
            fail("Creation failed.");
        }
    }


    @Test
    public void testCreateStreamCache() {
        try {
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), new NoCache());
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), new NoCache(), false);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), new NoCache(), true);
        } catch (Exception e) {
            fail("Creation failed.");
        }
    }

    @Test
    public void testCreateStreamSize() {
        try {
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, false);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, true);
        } catch (Exception e) {
            fail("Creation failed.");
        }
    }

    @Test
    public void testCreateStreamSizeCapacity() {
        try {
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, 1000);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, 1000, false);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, 1000, true);
        } catch (Exception e) {
            fail("Creation failed.");
        }
    }

    @Test
    public void testCreateStreamSizeCache() {
        try {
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, new NoCache());
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, new NoCache(), false);
            new InputStreamReader(new ByteArrayInputStream(new byte[1]), 1024, new NoCache(), true);
        } catch (Exception e) {
            fail("Creation failed.");
        }
    }

    @Test
    public void testToString() {
        assertTrue(readers[0].toString().contains("InputStreamReader"));
        assertTrue(readers[0].toString().contains("cache"));
    }

    private void testRandomPositions(String description, RandomAccessFile raf, WindowReader reader,
                                     int fileLength) throws IOException {
        // testReadByte randomly selected positions:
        for (int count = 0; count < 500; count++) {
            final int randomPosition = rand.nextInt(fileLength);
            raf.seek(randomPosition);
            byte fileByte = raf.readByte();
            assertEquals(description + randomPosition, fileByte,
                    (byte) reader.readByte(randomPosition));
        }
    }

    private void testReadByte(WindowReader reader, long position, byte value) throws IOException {
        assertEquals("Reader " + reader + " reading at position " + position + " should have value " + value,
                value, (byte) reader.readByte(position));
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

