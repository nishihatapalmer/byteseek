/*
 * Copyright Matt Palmer 2019, All rights reserved.
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
package net.byteseek.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import static org.junit.Assert.*;

public class IOUtilsTest {

    Random rand = new Random(); //TODO: log seed
    File asciiFile;
    InputStream stream;
    RandomAccessFile raf;
    FileChannel fileChannel;
    byte[] fileBytes;
    int fileLength;

    @Before
    public void setUp() throws Exception {
        asciiFile = getFile("/TestASCII.txt");
        stream    = new FileInputStream(asciiFile);
        raf       = new RandomAccessFile(asciiFile, "r");
        fileLength = (int) raf.length();
        fileBytes = new byte[fileLength];
        raf.readFully(fileBytes);
        raf.seek(0);
        fileChannel = raf.getChannel();
    }

    @After
    public void after() throws Exception {
        stream.close();
        fileChannel.close();
        raf.close();
    }

    //---------------------------------------------------------------------------

    @Test
    public void readBytesInputStreamToEmptyByteArray() throws Exception {
        final byte[] emptyArray = new byte[0];
        final int bytesCopied = IOUtils.readBytes(stream, emptyArray);
        assertEquals("No bytes copied to empty array", 0, bytesCopied);
    }

    @Test
    public void readBytesInputStreamToByteArrayBufferLarger() throws Exception {
        final byte[] larger = new byte[fileLength + 256];
        final int bytesCopied = IOUtils.readBytes(stream, larger);
        assertEquals("Only file length bytes copied", fileLength, bytesCopied);

        final byte[] another = new byte[fileLength + 256];
        raf.read(another);
        assertArrayEquals("Bytes copied are identical to reading from random access file", another, larger);
    }

    @Test
    public void readBytesInputStreamToByteArrayPastEndReturnsZero() throws Exception {
        final byte[] larger = new byte[fileLength + 256];
        final int bytesCopied = IOUtils.readBytes(stream, larger);
        assertEquals("Only file length bytes copied", fileLength, bytesCopied);

        final byte[] another = new byte[fileLength + 256];
        final int noBytesCopied = IOUtils.readBytes(stream, another);
        assertEquals("No bytes copied reading past end of stream.", 0, noBytesCopied);
    }

    @Test
    public void readBytesInputStreamToByteArrayBufferSmaller() throws Exception {
        final byte[] smaller = new byte[fileLength - 256];
        final int bytesCopied = IOUtils.readBytes(stream, smaller);
        assertEquals("Only buffer length bytes copied", smaller.length, bytesCopied);

        final byte[] another = new byte[fileLength - 256];
        raf.read(another);
        assertArrayEquals("Bytes copied are identical to reading from random access file", another, smaller);
    }

    @Test
    public void readBytesInputStreamToByteArrayBufferRepeatReads() throws Exception {
        final byte[] buffer = new byte[256];
        final byte[] another = new byte[256];

        int bytesCopied = IOUtils.readBytes(stream, buffer);
        assertEquals("Only buffer length bytes copied", buffer.length, bytesCopied);

        raf.read(another);
        assertArrayEquals("Bytes copied are identical to reading from random access file", another, buffer);

        bytesCopied = IOUtils.readBytes(stream, buffer);
        assertEquals("Only buffer length bytes copied", buffer.length, bytesCopied);

        raf.read(another);
        assertArrayEquals("Bytes copied are identical to reading from random access file", another, buffer);
    }

    //---------------------------------------------------------------------------

    @Test
    public void readBytesRandomAccessFileToEmptyByteArray() throws Exception {
        final byte[] buffer = new byte[0];
        final int bytesRead = IOUtils.readBytes(raf, buffer);
        assertEquals(0, bytesRead);
    }

    @Test
    public void readBytesRandomAccessFileToLargerByteArray() throws Exception {
        final byte[] buffer = new byte[fileLength + 256];
        final int bytesRead = IOUtils.readBytes(raf, buffer);
        assertEquals(fileLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, fileLength);
    }

    @Test
    public void readBytesRandomAccessFileToSmallerByteArray() throws Exception {
        final int smallLength = fileLength - 256;
        final byte[] buffer = new byte[smallLength];
        final int bytesRead = IOUtils.readBytes(raf, buffer);
        assertEquals(smallLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, smallLength);
    }

    @Test
    public void readBytesRandomAccessFileReadPastEndReturnsZero() throws Exception {
        final byte[] buffer = new byte[fileLength];
        IOUtils.readBytes(raf, buffer);
        assertEquals(0, IOUtils.readBytes(raf, buffer));
    }

    @Test
    public void readBytesRandomAccessFileRepeatReads() throws Exception {
        final byte[] buffer = new byte[255];
        int bytesRead = IOUtils.readBytes(raf,buffer);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, buffer.length);

        bytesRead = IOUtils.readBytes(raf, buffer);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer.length, buffer, buffer.length);
    }

    //---------------------------------------------------------------------------

    @Test
    public void readBytesRandomAccessFilePosToEmptyByteArray() throws Exception {
        final byte[] buffer = new byte[0];
        final int bytesRead = IOUtils.readBytes(raf, 0, buffer);
        assertEquals(0, bytesRead);
    }

    @Test
    public void readBytesRandomAccessFilePosToLargerByteArray() throws Exception {
        final byte[] buffer = new byte[fileLength + 256];
        int bytesRead = IOUtils.readBytes(raf, 0, buffer);
        assertEquals(fileLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, fileLength);

        bytesRead = IOUtils.readBytes(raf, 256, buffer);
        assertEquals(fileLength - 256, bytesRead);
        assertPartialArrayEquals(fileBytes, 256, buffer, fileLength - 256);
    }

    @Test
    public void readBytesRandomAccessFilePosToSmallerByteArray() throws Exception {
        final int smallLength = fileLength - 256;
        final byte[] buffer = new byte[smallLength];
        final int bytesRead = IOUtils.readBytes(raf, 0, buffer);
        assertEquals(smallLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, smallLength);
    }

    @Test
    public void readBytesRandomAccessFilePosReadPastEndReturnsZero() throws Exception {
        final byte[] buffer = new byte[fileLength];
        assertEquals(0, IOUtils.readBytes(raf, fileLength, buffer));
    }

    @Test
    public void readBytesRandomAccessFilePosRepeatReads() throws Exception {
        final byte[] buffer = new byte[255];
        int bytesRead = IOUtils.readBytes(raf, 0, buffer);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, buffer.length);

        bytesRead = IOUtils.readBytes(raf, 0, buffer);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, buffer.length);
    }


    //---------------------------------------------------------------------------

    //TODO: need tests for the positioning of the contents of the byte array, not just the
    // existing ones.


    @Test
    public void readBytesRandomAccessFilePosToEmptyByteArrayPos() throws Exception {
        final byte[] buffer = new byte[0];
        final int bytesRead = IOUtils.readBytes(raf, 0, buffer, 0, 1);
        assertEquals(0, bytesRead);
    }

    @Test
    public void readBytesRandomAccessFilePosToLargerByteArrayPos() throws Exception {
        final byte[] buffer = new byte[fileLength + 256];
        int bytesRead = IOUtils.readBytes(raf, 0, buffer, 0, buffer.length);
        assertEquals(fileLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, fileLength);

        bytesRead = IOUtils.readBytes(raf, 256, buffer, 0, buffer.length);
        assertEquals(fileLength - 256, bytesRead);
        assertPartialArrayEquals(fileBytes, 256, buffer, fileLength - 256);
    }

    @Test
    public void readBytesRandomAccessFilePosToSmallerByteArrayPos() throws Exception {
        final int smallLength = fileLength - 256;
        final byte[] buffer = new byte[smallLength];
        final int bytesRead = IOUtils.readBytes(raf, 0, buffer, 0, buffer.length);
        assertEquals(smallLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, smallLength);
    }

    @Test
    public void readBytesRandomAccessFileBytePosReadPastEndReturnsZero() throws Exception {
        final byte[] buffer = new byte[fileLength];
        assertEquals(0, IOUtils.readBytes(raf, fileLength, buffer, 0, buffer.length));
    }

    @Test
    public void readBytesRandomAccessFileBytePosRepeatReads() throws Exception {
        final byte[] buffer = new byte[255];
        int bytesRead = IOUtils.readBytes(raf, 0, buffer, 0, buffer.length);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, buffer.length);

        bytesRead = IOUtils.readBytes(raf, 256, buffer, 128, buffer.length);
        assertEquals(buffer.length - 128, bytesRead);
        assertPartialArrayEquals(fileBytes, 256, buffer, 128, buffer.length - 128);
    }

    //---------------------------------------------------------------------------


    @Test
    public void readBytesFileChannelToByteBuffer() throws Exception {
    }

    @Test
    public void readBytesFileChannelToEmptyByteBuffer() throws Exception {
        final byte[] buffer = new byte[0];
        ByteBuffer buf2 = ByteBuffer.wrap(buffer);
        final int bytesRead = IOUtils.readBytes(fileChannel, 0, buf2);
        assertEquals(0, bytesRead);
    }

    @Test
    public void readBytesFileChannelToLargerByteBuffer() throws Exception {
        final byte[] buffer = new byte[fileLength + 256];
        ByteBuffer buf2 = ByteBuffer.wrap(buffer);

        int bytesRead = IOUtils.readBytes(fileChannel, 0, buf2);
        assertEquals(fileLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, fileLength);

        buf2.clear();

        bytesRead = IOUtils.readBytes(fileChannel, 256, buf2);
        assertEquals(fileLength - 256, bytesRead);
        assertPartialArrayEquals(fileBytes, 256, buffer, fileLength - 256);
    }

    @Test
    public void readBytesFileChannelToSmallerByteBuffer() throws Exception {
        final int smallLength = fileLength - 256;
        final byte[] buffer = new byte[smallLength];
        ByteBuffer buf2 = ByteBuffer.wrap(buffer);

        final int bytesRead = IOUtils.readBytes(fileChannel, 0, buf2);
        assertEquals(smallLength, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, smallLength);
    }

    @Test
    public void readBytesFileChannelReadPastEndReturnsZero() throws Exception {
        final byte[] buffer = new byte[fileLength];
        ByteBuffer buf2 = ByteBuffer.wrap(buffer);

        assertEquals(0, IOUtils.readBytes(fileChannel, fileLength, buf2));
    }

    @Test
    public void readBytesFileChannelRepeatReads() throws Exception {
        final byte[] buffer = new byte[255];
        ByteBuffer buf2 = ByteBuffer.wrap(buffer);

        int bytesRead = IOUtils.readBytes(fileChannel, 0, buf2);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, buffer.length);

        buf2.clear();

        bytesRead = IOUtils.readBytes(fileChannel, 0, buf2);
        assertEquals(buffer.length, bytesRead);
        assertPartialArrayEquals(fileBytes, buffer, buffer.length);
    }

    //---------------------------------------------------------------------------


    @Test
    public void createTempFile() throws Exception {
        File temp = IOUtils.createTempFile();
        assertTrue( temp.getName().contains("byteseek"));
        assertTrue( temp.getName().contains(".tmp"));
    }

    //---------------------------------------------------------------------------


    @Test
    public void createTempFileInNullDir() throws Exception {
        File temp = IOUtils.createTempFile(null);
        try {
        assertTrue( temp.getName().contains("byteseek"));
        assertTrue( temp.getName().contains(".tmp"));
        } finally {
            temp.delete();
        }
    }


    @Test
    public void createTempFileInResourceDir() throws Exception {
        File dir = getFile("/");
        File temp = IOUtils.createTempFile(dir);
        try {
            assertTrue(temp.getName().contains("byteseek"));
            assertTrue(temp.getName().contains(".tmp"));
        } finally {
            temp.delete();
        }
    }

    @Test(expected = IOException.class)
    public void createTempFileNotInDirectory() throws Exception {
        File dir = getFile("/TestASCII.txt");
        File temp = IOUtils.createTempFile(dir);
    }

    //---------------------------------------------------------------------------


    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }

    private void assertPartialArrayEquals(byte[] expected, byte[] toTest, int length) {
        assertPartialArrayEquals(expected, 0, toTest, length);
    }

    private void assertPartialArrayEquals(byte[] expected, int from, byte[] toTest, int length) {
        for (int i = 0; i < length; i++) {
            assertEquals(expected[from + i], toTest[i]);
        }
    }

    private void assertPartialArrayEquals(byte[] expected, int from, byte[] toTest, int bytefrom, int length) {
        for (int i = 0; i < length; i++) {
            assertEquals(expected[from + i], toTest[bytefrom + i]);
        }
    }

}