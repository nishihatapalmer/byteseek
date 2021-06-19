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

import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.windows.Window;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.Assert.*;

//TODO: add tests for different start and end positions.

public class ByteArrayIOIteratorTest {

    private RandomAccessFile raf;
    private WindowReader reader;
    private ByteArrayIOIterator iterator;

    @Before
    public void setup() throws IOException {
        File file = getFile("/TestASCII.txt");
        reader = new FileReader(file);
        raf = new RandomAccessFile(file, "r");
        iterator = new ByteArrayIOIterator(reader);
    }

    @After
    public void teardown() throws IOException {
        reader.close();
        raf.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullWindowReader() {
        new WindowIterator(null);
    }

    @Test
    public void hasNext() throws Exception {
        assertTrue(iterator.hasNext());
    }

    @Test
    public void doesNotHasNext() throws Exception {
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void next() throws Exception {
        long position = 0;
        while (iterator.hasNext()) {
            byte[] array = iterator.next();
            byte[] expected = new byte[array.length];
            int bytesRead = IOUtils.readBytes(raf, position, expected);
            assertEquals(array.length, bytesRead);
            assertArrayEquals(expected, array);
            position += array.length;
        }
    }

    @Test
    public void testFromPosition() throws Exception {
        for (long position = 0; position < reader.length(); position += 3956) {
            testFromPosition(position, reader.length() - position);
        }
    }

    @Test
    public void testFromToPosition() throws Exception {
        Random random = new Random();
        final int length = (int) reader.length();
        for (int testIteration = 0; testIteration < 100; testIteration++) {
            int fromPosition = random.nextInt(length - 1);
            int lengthToRead = random.nextInt(length / 2);
            testFromToPosition(fromPosition, fromPosition + lengthToRead);
        }
    }

    private void testFromPosition(long fromPosition, long expectedLength) throws Exception {
        iterator = new ByteArrayIOIterator(reader, fromPosition);
        long position = fromPosition;
        int totalBytesRead = 0;
        while (iterator.hasNext()) {
            byte[] array = iterator.next();
            byte[] expected = new byte[array.length];
            int bytesRead = IOUtils.readBytes(raf, position, expected);
            assertEquals(array.length, bytesRead);
            assertArrayEquals(expected, array);
            totalBytesRead += array.length;
            position += array.length;
        }
        assertEquals(expectedLength, totalBytesRead);
    }

    private void testFromToPosition(long fromPosition, long toPosition) throws Exception {
        iterator = new ByteArrayIOIterator(reader, fromPosition, toPosition);
        long expectedLength = toPosition >= reader.length() ? reader.length() - fromPosition : toPosition - fromPosition + 1;
        long position = fromPosition;
        int totalBytesRead = 0;
        while (iterator.hasNext()) {
            byte[] array = iterator.next();
            byte[] expected = new byte[array.length];
            int bytesRead = IOUtils.readBytes(raf, position, expected);
            assertEquals(array.length, bytesRead);
            assertArrayEquals(expected, array);
            totalBytesRead += array.length;
            position += array.length;
        }
        assertEquals(expectedLength, totalBytesRead);
    }

    @Test(expected = NoSuchElementException.class)
    public void noNext() throws Exception {
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() throws Exception {
        iterator.remove();
    }

    @Test
    public void testToString() {
        assertTrue(iterator.toString().contains(ByteArrayIOIterator.class.getSimpleName()));
        assertTrue(iterator.toString().contains(WindowIterator.class.getSimpleName()));
        assertTrue(iterator.toString().contains("reader"));
        assertTrue(iterator.toString().contains("position"));
    }

    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }

}