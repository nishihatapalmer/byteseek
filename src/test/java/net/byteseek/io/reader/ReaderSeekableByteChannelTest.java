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

package net.byteseek.io.reader;

import net.byteseek.io.reader.windows.Window;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.util.Random;

import static org.junit.Assert.*;

public class ReaderSeekableByteChannelTest {

    private final byte[] array = new byte[1024];
    private final ByteBuffer buffer = ByteBuffer.wrap(array);
    private WindowReader reader;
    private ReaderSeekableByteChannel channel;
    private long fileLength;

    @Before
    public void setup() throws IOException {
        File file = getFile("/TestASCII.txt");
        fileLength = file.length();
        reader = new FileReader(file);
        channel = new ReaderSeekableByteChannel(reader);
    }

    @After
    public void teardown() throws IOException {
        channel.close();
        reader.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullReader() {
        new ReaderSeekableByteChannel(null);
    }

    @Test
    public void readChannelFirst() throws Exception {
        for (long position = 0; position < fileLength; position += array.length) {
            int bytesRead = channel.read(buffer);
            Window window = reader.getWindow(position);
            int offset = reader.getWindowOffset(position);
            assertArrayEquals(window.getArray(), array, offset, bytesRead);
            buffer.clear();
        }
    }

    @Test
    public void readReaderFirst() throws Exception {
        for (long position = 0; position < fileLength; position += array.length) {
            Window window = reader.getWindow(position);
            int offset = reader.getWindowOffset(position);
            int bytesRead = channel.read(buffer);
            assertArrayEquals(window.getArray(), array, offset, bytesRead);
            buffer.clear();
        }
    }

    private void assertArrayEquals(byte[] array0, byte[] array1, int offset, int length) {
        for (int i = 0; i < length; i++) {
            assertEquals(array0[offset + i], array1[i]);
        }
    }

    private void assertArrayEquals(byte[] array0, byte[] array1, int offset, int offset2, int length) {
        for (int i = 0; i < length; i++) {
            assertEquals(array0[offset + i], array1[offset2 + i]);
        }
    }

    @Test(expected = ClosedChannelException.class)
    public void testNoReadIfClosed() throws Exception {
        channel.close();
        channel.read(buffer);
    }

    @Test(expected = NonWritableChannelException.class)
    public void write() throws Exception {
        channel.write(buffer);
    }

    @Test
    public void position() throws Exception {
        for (long position = 0; position < fileLength; position += array.length) {
            assertEquals(position, channel.position());
            channel.read(buffer);
            buffer.clear();
        }
    }

    @Test(expected = ClosedChannelException.class)
    public void testPositionWhenClosed() throws Exception {
        channel.close();
        channel.position();
    }

    @Test
    public void testSetPosition() throws Exception {
        Random random = new Random();
        for (int test = 0; test < 1000; test++) {
            long position = random.nextInt((int) fileLength);
            channel.position(position);
            int bytesRead = channel.read(buffer);
            int bytesChecked = 0;
            while (bytesChecked < bytesRead) {
                Window window = reader.getWindow(position + bytesChecked);
                int offset = reader.getWindowOffset(position + bytesChecked);
                int windowBytesAvailable = window.length() - offset;
                int bytesRemaining = bytesRead - bytesChecked;
                int bytesToCheck = windowBytesAvailable < bytesRemaining ? windowBytesAvailable : bytesRemaining;
                assertArrayEquals(window.getArray(), array, offset, bytesChecked, bytesToCheck);
                bytesChecked += bytesToCheck;
            }
            buffer.clear();
        }
    }

    @Test(expected = ClosedChannelException.class)
    public void testSetPositionWhenClosed() throws Exception {
        channel.close();
        channel.position(2);
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(reader.length(), channel.size());
    }

    @Test(expected = ClosedChannelException.class)
    public void testNoSizeIfClosed() throws Exception {
        channel.close();
        channel.size();
    }

    @Test(expected = NonWritableChannelException.class)
    public void truncate() throws Exception {
        channel.truncate(1);
    }

    @Test(expected = NonWritableChannelException.class)
    public void truncateWhenClosed() throws Exception {
        channel.close();
        channel.truncate(1);
    }

    @Test
    public void isOpen() throws Exception {
        assertTrue(channel.isOpen());
        channel.close();
        assertFalse(channel.isOpen());
    }

    @Test(expected = ClosedChannelException.class)
    public void close() throws Exception {
        assertTrue(channel.isOpen());
        channel.close();
        assertFalse(channel.isOpen());
        ByteBuffer buffer = ByteBuffer.wrap(new byte[1024]);
        channel.read(buffer);
    }

    @Test
    public void testReaderIsClosedWhenChannelIsClosed() throws IOException {
        assertFalse(reader.isClosed());
        channel.close();
        assertTrue(reader.isClosed());
    }

    @Test
    public void testReaderIsNotClosedWhenChannelIsClosed() throws IOException {
        assertFalse(reader.isClosed());
        channel = new ReaderSeekableByteChannel(reader, false);
        assertFalse(reader.isClosed());
        channel.close();
        assertFalse(reader.isClosed());
    }

    @Test
    public void testToString() {
        assertTrue(channel.toString().contains(ReaderSeekableByteChannel.class.getSimpleName()));
        assertTrue(channel.toString().contains("reader"));
        assertTrue(channel.toString().contains("isClosed"));
        assertTrue(channel.toString().contains("position"));
    }

    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }

}